package com.peiyingr.yummyfit.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.web.multipart.MultipartFile;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.food.NutritionFactDTO;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService {

    public YummyFitResponse detectText(MultipartFile imageFile) throws IOException {
        YummyFitResponse response = new YummyFitResponse();
        NutritionFactDTO nutritionFacts = new NutritionFactDTO();
        
        String credentialsPath = "googleCloudVision.json";
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource(credentialsPath).getInputStream());

        ImageAnnotatorSettings visionSettings = ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(visionSettings)) {
            ByteString imgBytes = ByteString.copyFrom(imageFile.getBytes());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);
            BatchAnnotateImagesResponse batchResponse = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = batchResponse.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    response.setError(true);
                    response.setMessage(res.getError().getMessage());
                    return response;
                }

                String detectedText = res.getTextAnnotationsList().get(0).getDescription();
                parseText(detectedText, nutritionFacts);
                response.setData(nutritionFacts);
                response.setOk(true);
            }
        } catch (Exception e) {
            response.setError(true);
            response.setMessage("Error during Google Vision API call: " + e.getMessage());
        }
        return response;
    }

    private void parseText(String detectedText, NutritionFactDTO nutritionFacts) {
        String[] result = detectedText.split("\n");
        List<String> resultArray = new ArrayList<>();
        // 移除每個字串裡可能含有的"|"
        for (String text : result) {
            String textOk = text.replace("|", "");
            resultArray.add(textOk);
        }

        String[] keywords = {"Nutrition", "Amount", "per", "serving", "Total Fat", "Total Carbohydrate", "Protein"};

        List<String> keywordList = Arrays.asList(keywords);
        
        // 找尋resultArray中的字串有無keywords中的關鍵字
        boolean includesKeyword = resultArray.stream()
                .anyMatch(str -> keywordList.stream().anyMatch(str::contains));
        if (includesKeyword) {
            // USA nutrition facts
            parseUSANutritionFacts(resultArray, nutritionFacts);
        } else {
            // Taiwan nutrition facts
            parseTaiwanNutritionFacts(resultArray, nutritionFacts);
        }
    }

    private void parseUSANutritionFacts(List<String> resultArray, NutritionFactDTO nutritionFacts) {
        String servingSize = null;
        String perServingFat = null;
        String perServingCarbs = null;
        String perServingProtein = null;
    
        Float fatValue = null;
        Float carbsValue = null;
        Float proValue = null;
    
        for (String x : resultArray) {
            Matcher servingMatcher = Pattern.compile("\\((\\d+g?)\\)").matcher(x);
            if (servingMatcher.find()) {
                servingSize = servingMatcher.group(1).replace("g", "").trim();
            }

            Matcher fatMatcher = Pattern.compile("Total Fat (\\d+\\w*)").matcher(x);
            if (fatMatcher.find()) {
                perServingFat = fatMatcher.group(1).replace("g", "").trim();
            }
    
            Matcher carbsMatcher = Pattern.compile("Total Carbohydrate (\\d+\\w*)").matcher(x);
            if (carbsMatcher.find()) {
                perServingCarbs = carbsMatcher.group(1).replace("g", "").trim();
            }
    
            Matcher proteinMatcher = Pattern.compile("Protein (\\d+\\w*)").matcher(x);
            if (proteinMatcher.find()) {
                perServingProtein = proteinMatcher.group(1).replace("g", "").trim();
            }
        }
        System.out.println(perServingProtein);
        if (servingSize != null) {
            float servingSizeValue = Float.parseFloat(servingSize);
    
            if (perServingFat != null) {
                float fatPerServing = Float.parseFloat(perServingFat);
                fatValue = Math.round((fatPerServing / servingSizeValue) * 1000) / 10.0f;
            }
    
            if (perServingCarbs != null) {
                float carbsPerServing = Float.parseFloat(perServingCarbs);
                carbsValue = Math.round((carbsPerServing / servingSizeValue) * 1000) / 10.0f;
            }
    
            if (perServingProtein != null) {
                float proteinPerServing = Float.parseFloat(perServingProtein);
                proValue = Math.round((proteinPerServing / servingSizeValue) * 1000) / 10.0f;
            }
        }
    
        nutritionFacts.setName(null);
        nutritionFacts.setProValue(proValue != null ? proValue.toString() : null);
        nutritionFacts.setFatValue(fatValue != null ? fatValue.toString() : null);
        nutritionFacts.setCarbsValue(carbsValue != null ? carbsValue.toString() : null);
    ;
    }
    
    private void parseTaiwanNutritionFacts(List<String> resultArray, NutritionFactDTO nutritionFacts) {
        //移除array裡的"營養標示"
        resultArray.remove("營養標示");
        int indexOfKcalText = resultArray.indexOf("熱量");
        int indexOfProText = resultArray.indexOf("蛋白質");
        int indexOfFatText = resultArray.indexOf("脂肪");
        int indexOfCarbsText = resultArray.indexOf("碳水化合物");
        int proKcalGap = indexOfProText - indexOfKcalText;
        int fatKcalGap = indexOfFatText - indexOfKcalText;
        int carbsKcalGap = indexOfCarbsText - indexOfKcalText;
    
        // 找品名name
        String name = null;
        Pattern regexProductName = Pattern.compile("品名:\\s*(.*)"); // 匹配 "品名:" 和後面的內容
        for (String x : resultArray) {
            Matcher matcher = regexProductName.matcher(x);
            if (matcher.find()) {
                name = matcher.group(1).trim(); // 提取 "品名:" 後的文本，並去除首尾空白
                break; // 找到第一個品名後即可跳出循環
            }
        }

        // 找出每100公克或100毫升的index
        Pattern regexGram = Pattern.compile("^每\\s*100\\s*公克$");
        Pattern regexML = Pattern.compile("^每\\s*100\\s*毫升$");
        Integer indexOfPerHundredUnit = null;
    
        for (int i = 0; i < resultArray.size(); i++) {
            String x = resultArray.get(i);
            if (regexGram.matcher(x).matches() || regexML.matcher(x).matches()) {
                indexOfPerHundredUnit = i;
                break;
            }
        }
    
        String proValue = null;
        String fatValue = null;
        String carbsValue = null;
    
        if (indexOfPerHundredUnit != null) {
            // System.out.println(resultArray);
            proValue = getValueAtIndex(resultArray, indexOfPerHundredUnit + 3 + proKcalGap);
            // System.out.println(indexOfPerHundredUnit);
            // System.out.println(proKcalGap);
            // System.out.println(proValue);
            fatValue = getValueAtIndex(resultArray, indexOfPerHundredUnit + 3 + fatKcalGap);
            carbsValue = getValueAtIndex(resultArray, indexOfPerHundredUnit + 3 + carbsKcalGap);
        } else {
            // 找出每100公克或100毫升「大卡值」的index
            Pattern regexKcal = Pattern.compile("^\\d+(\\.\\d+)?\\s*大卡$");
            Integer indexOfKcalValue = null;
            for (int i = 0; i < resultArray.size(); i++) {
                String x = resultArray.get(i);
                if (regexKcal.matcher(x).matches()) {
                    indexOfKcalValue = i;
                    break;
                }
            }
            if (indexOfKcalValue != null) {
                proValue = getValueAtIndex(resultArray, indexOfKcalValue + proKcalGap);
                fatValue = getValueAtIndex(resultArray, indexOfKcalValue + fatKcalGap);
                carbsValue = getValueAtIndex(resultArray, indexOfKcalValue + carbsKcalGap);
            }
        }
    
        proValue = removeUnit(proValue, "公克");
        fatValue = removeUnit(fatValue, "公克");
        carbsValue = removeUnit(carbsValue, "公克");
    
        nutritionFacts.setName(name);
        nutritionFacts.setProValue(proValue);
        nutritionFacts.setFatValue(fatValue);
        nutritionFacts.setCarbsValue(carbsValue);
    }
    
    private String getValueAtIndex(List<String> list, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }
    
    private String removeUnit(String value, String unit) {
        if (value != null && value.contains(unit)) {
            return value.replace(unit, "").trim();
        }
        return value;
    }    
}
