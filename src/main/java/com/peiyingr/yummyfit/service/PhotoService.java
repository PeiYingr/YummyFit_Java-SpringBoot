package com.peiyingr.yummyfit.service;

import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.mealRecord.MealRecordDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.dto.photo.MealPhotoDTO;
import com.peiyingr.yummyfit.dto.photo.DeleteMealPhotoDTO;
import com.peiyingr.yummyfit.entity.MealPhoto;
import com.peiyingr.yummyfit.entity.MealRecord;
import com.peiyingr.yummyfit.entity.User;
import com.peiyingr.yummyfit.repository.MealRecordRepository;
import com.peiyingr.yummyfit.repository.AvatarPhotoRepository;
import com.peiyingr.yummyfit.repository.MealPhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class PhotoService {
    
    @Autowired
    public AvatarPhotoRepository avatarPhotoRepository;

    @Autowired
    public MealPhotoRepository mealPhotoRepository;

    @Autowired
    MealRecordService mealRecordService;

    @Autowired
    MealRecordRepository mealRecordRepository;

    private final S3Client s3Client;
    
    public PhotoService(
        @Value("${aws.accessKeyId}") String accessKey, 
        @Value("${aws.secretAccessKey}") String secretKey
    ) {
        this.s3Client = S3Client.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .build();
    }

    public YummyFitResponse updateAvatar(MultipartFile imageFile, UserDTO userDTO) throws IOException {
        YummyFitResponse response = new YummyFitResponse();
        try {
            Integer userId = userDTO.getUserId();
            // Generate a 32 character alpha-numeric token as file name
            String fileName = generateRandomToken(32);
            // 設定 S3 物件的參數
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket("peiprojectbucket") 
                .key("avatar/" + fileName)  // S3 folder name + file name(檔案名稱)
                .contentType(imageFile.getContentType())
                .build();
            PutObjectResponse s3Response = s3Client.putObject(request, RequestBody.fromBytes(((imageFile.getBytes()))));
            
            String s3Url = "https://peiprojectbucket.s3.amazonaws.com/avatar/" + fileName;
            String cloudFrontUrl = s3Url.replace("https://peiprojectbucket.s3.amazonaws.com", "https://dle57qor2pt0d.cloudfront.net");
            if (s3Url.contains("https://peiprojectbucket.s3.us-west-2.amazonaws.com")) {
                cloudFrontUrl = s3Url.replace("https://peiprojectbucket.s3.us-west-2.amazonaws.com", "https://dle57qor2pt0d.cloudfront.net");
            }
        
            avatarPhotoRepository.updateUAvatar(cloudFrontUrl, userId);
            response.setData(cloudFrontUrl);
            response.setOk(true);
        } catch (S3Exception e) {
            System.err.println("S3 upload error: " + e.awsErrorDetails().errorMessage());
            response.setError(true);
            response.setMessage("Error uploading to S3: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            System.err.println("File read error: " + e.getMessage());
            response.setError(true);
            response.setMessage("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            response.setError(true);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
        }

        return response;
    }

    public YummyFitResponse getAvatar(UserDTO userDTO) {
        YummyFitResponse response = new YummyFitResponse();
        Integer userId = userDTO.getUserId();
        User userInfo = avatarPhotoRepository.findUserAvatarByUserId(userId);

        if (userInfo.getAvatar() != null) {
            response.setData(userInfo.getAvatar());
            response.setOk(true);
        } else {
            response.setData(null);
            response.setOk(true);
        }
        return response;
    }


    public List<MealPhotoDTO> getMealPhoto(List<MealRecordDTO> mealRecordDTOList) {
        List<MealRecord> mealRecordList = new ArrayList<>();
        for (MealRecordDTO mealRecordDTO : mealRecordDTOList) {
            MealRecord mealRecord = new MealRecord();
            BeanUtils.copyProperties(mealRecordDTO, mealRecord);
            mealRecordList.add(mealRecord);
        }
        List<MealPhoto> photoInfo = mealPhotoRepository.getMealPhoto(mealRecordList);
        if (!photoInfo.isEmpty()) {
            List<MealPhotoDTO> mealPhotoDTOList = new ArrayList<>();
            for (MealPhoto mealPhoto : photoInfo) {
                MealPhotoDTO mealPhotoDTO = new MealPhotoDTO();
                BeanUtils.copyProperties(mealPhoto, mealPhotoDTO);
                mealPhotoDTOList.add(mealPhotoDTO);
            }
            return mealPhotoDTOList;
        } else {
            return null;
        }
    }


    public List<MealPhotoDTO> storeMealPhotos(List<MultipartFile> imageFiles, UserDTO userDTO, String date, String meal) {
        uploadFiles(imageFiles, userDTO, date, meal);
        List<MealRecordDTO> mealRecord = mealRecordService.searchMealRecord(userDTO, date, meal);
        List<MealPhotoDTO> mealPhotoDTOList = getMealPhoto(mealRecord);
        if (!mealPhotoDTOList.isEmpty()) {
            return mealPhotoDTOList;
        } else {
            return null;
        }
    }

    private void uploadFiles(List<MultipartFile> imageFiles, UserDTO userDTO, String date, String meal) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        List<MealRecord> mealRecordList = mealRecordRepository.searchMealRecordIdByUserIdDateMeal(user, date, meal);
        if(mealRecordList.isEmpty()) {
            MealRecord mealRecord = new MealRecord();
            mealRecord.setDate(date);
            mealRecord.setMeal(meal);
            mealRecord.setUserId(user);
            mealRecordRepository.save(mealRecord);
            mealRecordList = mealRecordRepository.searchMealRecordIdByUserIdDateMeal(user, date, meal);
        }
        MealRecord mealRecord = mealRecordList.get(0);

        List<CompletableFuture<String>> promises = new ArrayList<>();
        // 處理多個文件上傳
        for (MultipartFile imageFile : imageFiles) {
            promises.add(CompletableFuture.supplyAsync(() -> {
                try {
                    // Generate a 32 character alpha-numeric token as file name
                    String fileName = generateRandomToken(32);
                    // 設定 S3 物件的參數
                    PutObjectRequest request = PutObjectRequest.builder()
                        .bucket("peiprojectbucket") 
                        .key("userMeal/" + fileName)  // S3 folder name + file name(檔案名稱)
                        .contentType(imageFile.getContentType())
                        .build();
                    PutObjectResponse s3Response = s3Client.putObject(request, RequestBody.fromBytes(((imageFile.getBytes()))));
                    
                    String s3Url = "https://peiprojectbucket.s3.amazonaws.com/userMeal/" + fileName;
                    String mealCloudFrontUrl = s3Url.replace("https://peiprojectbucket.s3.amazonaws.com", "https://dle57qor2pt0d.cloudfront.net");
                    if (s3Url.contains("https://peiprojectbucket.s3.us-west-2.amazonaws.com")) {
                        mealCloudFrontUrl = s3Url.replace("https://peiprojectbucket.s3.us-west-2.amazonaws.com", "https://dle57qor2pt0d.cloudfront.net");
                    }
                    MealPhoto mealRecordPhoto = new MealPhoto();
                    mealRecordPhoto.setMealRecordId(mealRecord);
                    mealRecordPhoto.setPhoto(mealCloudFrontUrl);
                    mealPhotoRepository.save(mealRecordPhoto);
                    return mealCloudFrontUrl;
                } catch (IOException e) {
                    throw new RuntimeException("Error uploading file", e);
                }
            }));
        }
        // 等待所有的文件上傳完成
        CompletableFuture.allOf(promises.toArray(new CompletableFuture[0])).join();
    }

    public DeleteMealPhotoDTO deleteMealPhoto(UserDTO userDTO, String mealPhotoId) {
        Integer userId = mealPhotoRepository.findUserByMealPhotoId(mealPhotoId);
        if (userId.equals(userDTO.getUserId())){
            mealPhotoRepository.deleteMealPhoto(mealPhotoId);
        }

        DeleteMealPhotoDTO deleteMealPhotoDTO = new DeleteMealPhotoDTO();
        deleteMealPhotoDTO.setMealPhotoId(mealPhotoId);
        return deleteMealPhotoDTO;
    }

    private String generateRandomToken(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}