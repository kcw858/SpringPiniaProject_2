package com.sist.web.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecipeAPI {
    public static void main(String[] args) {

        String serviceKey = "e419b948843f446a9597";

        // API 1회 반환 제한 건수
        int fetchSize = 5;

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            //conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            //conn.setAutoCommit(false);

            String mergeRecipeSql = "MERGE INTO RECIPE r "
                    + "USING (SELECT ? AS rcp_seq FROM dual) src "
                    + "ON (r.RCP_SEQ = src.rcp_seq) "
                    + "WHEN MATCHED THEN "
                    + "  UPDATE SET RCP_NM=?, RCP_WAY2=?, RCP_PAT2=?, INFO_WGT=?, INFO_ENG=?, INFO_CAR=?, "
                    + "             INFO_PRO=?, INFO_FAT=?, INFO_NA=?, HASH_TAG=?, ATT_FILE_NO_MAIN=?, "
                    + "             ATT_FILE_NO_MK=?, RCP_PARTS_DTLS=?, RCP_NA_TIP=? "
                    + "WHEN NOT MATCHED THEN "
                    + "  INSERT (RCP_SEQ, RCP_NM, RCP_WAY2, RCP_PAT2, INFO_WGT, INFO_ENG, INFO_CAR, INFO_PRO, "
                    + "          INFO_FAT, INFO_NA, HASH_TAG, ATT_FILE_NO_MAIN, ATT_FILE_NO_MK, RCP_PARTS_DTLS, RCP_NA_TIP) "
                    + "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String mergeManualSql = "MERGE INTO RECIPE_MANUAL m "
                    + "USING (SELECT ? AS rcp_seq, ? AS step_no FROM dual) src "
                    + "ON (m.RCP_SEQ = src.rcp_seq AND m.STEP_NO = src.step_no) "
                    + "WHEN MATCHED THEN "
                    + "  UPDATE SET MANUAL_DESC=?, MANUAL_IMG=? "
                    + "WHEN NOT MATCHED THEN "
                    + "  INSERT (RCP_SEQ, STEP_NO, MANUAL_DESC, MANUAL_IMG) "
                    + "  VALUES (?, ?, ?, ?)";

            //psRecipe = conn.prepareStatement(mergeRecipeSql);
            //psManual = conn.prepareStatement(mergeManualSql);
            ObjectMapper mapper = new ObjectMapper();

            int startIdx = 1;
            int totalCount = 0;
            int totalSavedRecipeCount = 0;

            System.out.println("=== 전체 레시피 데이터 수집을 시작합니다. ===");

            while (true) {
                int endIdx = startIdx + fetchSize - 1;
                String urlStr = "http://openapi.foodsafetykorea.go.kr/api/" + serviceKey 
                              + "/COOKRCP01/json/" + startIdx + "/" + endIdx;

                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setRequestMethod("GET");
                    urlConn.setConnectTimeout(5000);
                    urlConn.setReadTimeout(10000);

                    int responseCode = urlConn.getResponseCode();
                    if (responseCode != 200) {
                        System.out.println("API 호출 응답 오류 (코드: " + responseCode + ") - 구간: " + startIdx + "~" + endIdx);
                        startIdx += fetchSize;
                        continue;
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
                    StringBuilder jsonResult = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        jsonResult.append(line);
                    }
                    br.close();
                    urlConn.disconnect();

                    JsonNode rootNode = mapper.readTree(jsonResult.toString());
                    JsonNode cookRcpNode = rootNode.path("COOKRCP01");

                    // 총 데이터 수 최초 1회 확인
                    if (totalCount == 0 && cookRcpNode.has("total_count")) {
                        totalCount = cookRcpNode.path("total_count").asInt();
                        System.out.println("API 전체 등록 데이터 수: " + totalCount + "건");
                    }

                    JsonNode rowArray = cookRcpNode.path("row");

                    if (rowArray.isArray() && rowArray.size() > 0) {
                        for (JsonNode row : rowArray) {
                            long rcpSeq = row.path("RCP_SEQ").asLong();
                            String rcpNm = getStringOrNull(row, "RCP_NM");
                            String rcpWay2 = getStringOrNull(row, "RCP_WAY2");
                            String rcpPat2 = getStringOrNull(row, "RCP_PAT2");
                            String infoWgt = getStringOrNull(row, "INFO_WGT");
                            Double infoEng = getDoubleOrNull(row, "INFO_ENG");
                            Double infoCar = getDoubleOrNull(row, "INFO_CAR");
                            Double infoPro = getDoubleOrNull(row, "INFO_PRO");
                            Double infoFat = getDoubleOrNull(row, "INFO_FAT");
                            Double infoNa = getDoubleOrNull(row, "INFO_NA");
                            String hashTag = getStringOrNull(row, "HASH_TAG");
                            String mainImg = getStringOrNull(row, "ATT_FILE_NO_MAIN");
                            String mkImg = getStringOrNull(row, "ATT_FILE_NO_MK");
                            String parts = getStringOrNull(row, "RCP_PARTS_DTLS");
                            String naTip = getStringOrNull(row, "RCP_NA_TIP");
                            System.out.println("========================");
                            System.out.println(rcpSeq);
                            List<IngredientVO> a = parse(parts);
                            for(int k = 0; k < a.size(); k++)
                            {
                            	System.out.println(a.get(k));
                            }
                           
                            System.out.println("========================");
                        }

                
                        // 지정된 전체 수량에 도달하면 수집 종료
                        if (totalCount > 0 && endIdx >= totalCount) {
                            System.out.println("모든 데이터 수집이 성공적으로 완료되었습니다.");
                            break;
                        }

                    } else {
                        // 데이터가 없는 구간 발생 시 종료 처리
                        if (totalCount > 0 && startIdx > totalCount) {
                            System.out.println("수집 범위 초과로 작업을 종료합니다.");
                            break;
                        }
                    }

                } catch (Exception e) {
                    
                }

                startIdx += fetchSize;
                
                // API 서버 호출 간격 조정 (0.1초)
                Thread.sleep(100);
            }

            System.out.println("=== 최종 수집 종료: 총 " + totalSavedRecipeCount + "개 레시피 저장 완료 ===");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            
        }
    }

    private static String getStringOrNull(JsonNode node, String fieldName) {
        if (!node.has(fieldName) || node.path(fieldName).isNull()) return null;
        String value = node.path(fieldName).asText().trim();
        return value.isEmpty() ? null : value;
    }

    private static Double getDoubleOrNull(JsonNode node, String fieldName) {
        String value = getStringOrNull(node, fieldName);
        if (value == null) return null;
        try { 
            return Double.parseDouble(value); 
        } catch (NumberFormatException e) { 
            return null; 
        }
    }

    private static void setDoubleSafe(PreparedStatement ps, int index, Double value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.NUMERIC);
        } else {
            ps.setDouble(index, value);
        }
    }
    private static final Pattern INGREDIENT_PATTERN =
            Pattern.compile(
                    "^(.+?)\\s*\\(?"
                    + "([0-9]+(?:\\.[0-9]+)?(?:/[0-9]+)?"
                    + "|[0-9]+⅓"
                    + "|[0-9]+⅔"
                    + "|[0-9]+½"
                    + "|[0-9]+¼"
                    + "|[0-9]+¾)"
                    + "\\s*"
                    + "(g|kg|mg|ml|l|L|개|알|쪽|장|모|봉|송이|줄기|큰술|작은술|컵|공기|근|마리)"
                    + "(?:\\s*\\(([^)]*)\\))?"
                    + "\\)?$",
                    Pattern.CASE_INSENSITIVE
            );

    /*
     * 숫자 없이 "약간", "적당량" 등
     */
    private static final Pattern TEXT_AMOUNT_PATTERN =
            Pattern.compile(
                    "^(.+?)\\s+(약간|적당량|조금|한줌|한 줌)$"
            );


    /**
     * API의 전체 재료 문자열을 받아
     * 재료 리스트로 변환
     */
    public static List<IngredientVO> parse(String text) {

        List<IngredientVO> result = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return result;
        }

        // ========================================
        // 1. 기본 전처리
        // ========================================

        // \r\n → \n
        text = text.replace("\r\n", "\n");

        // 실제 문자열 "\n" → 줄바꿈
        text = text.replace("\\n", "\n");

        // <br> → 줄바꿈
        text = text.replaceAll(
                "(?i)<br\\s*/?>",
                "\n"
        );

        // HTML 태그 제거
        //text = Jsoup.parse(text).text();

        // ========================================
        // 2. ● • · 기준으로 줄 분리
        // ========================================

        text = text.replaceAll(
                "\\s*[●•·]\\s*",
                "\n"
        );

        // ========================================
        // 3. 불필요한 공백 정리
        // ========================================

        text = text.replaceAll(
                "[ \\t]+",
                " "
        );

        // ========================================
        // 4. 줄 단위 처리
        // ========================================

        String[] lines = text.split("\\n");

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            // ====================================
            // 첫 번째 줄이 제목인 경우
            // ====================================

            if (i == 0 && isRecipeTitle(line)) {
                continue;
            }

            // ====================================
            // "고명", "육수", "양념장" 같은 단독 제목
            // ====================================

            if (isSectionTitle(line)) {
                continue;
            }

            // ====================================
            // "필수재료 : ..."
            // "육수 : ..."
            // "양념장 : ..."
            //
            // 앞의 제목 제거
            // ====================================

            line = removeSectionTitle(line);

            if (line.isBlank()) {
                continue;
            }

            // ====================================
            // 5. 쉼표 기준으로 재료 분리
            // ====================================

            String[] ingredients =
                    line.split("\\s*,\\s*");

            for (String ingredient : ingredients) {

                ingredient = ingredient.trim();

                if (ingredient.isEmpty()) {
                    continue;
                }

                IngredientVO vo =
                        parseIngredient(ingredient);

                if (vo != null) {
                    result.add(vo);
                }
            }
        }

        return result;
    }


    /**
     * 재료 하나 파싱
     */
    private static IngredientVO parseIngredient(
            String text) {

        text = text.trim();

        // ========================================
        // 1. 숫자 + 단위가 있는 경우
        // ========================================

        Matcher matcher =
                INGREDIENT_PATTERN.matcher(text);

        if (matcher.matches()) {

            String name =
                    matcher.group(1).trim();

            String amountString =
                    matcher.group(2);

            String unit =
                    matcher.group(3);

            String amountText =
                    matcher.group(4);

            IngredientVO vo =
                    new IngredientVO();

            vo.setOriginal(text);
            vo.setName(name);
            vo.setAmount(
                    convertNumber(amountString)
            );
            vo.setUnit(unit);
            vo.setAmountText(amountText);

            return vo;
        }

        // ========================================
        // 2. "소금 약간"
        // ========================================

        Matcher textMatcher =
                TEXT_AMOUNT_PATTERN.matcher(text);

        if (textMatcher.matches()) {

            IngredientVO vo =
                    new IngredientVO();

            vo.setOriginal(text);

            vo.setName(
                    textMatcher.group(1).trim()
            );

            vo.setAmount(null);

            vo.setUnit(null);

            vo.setAmountText(
                    textMatcher.group(2)
            );

            return vo;
        }

        // ========================================
        // 3. 파싱 실패
        //
        // 버리지 않고 원본 그대로 저장
        // ========================================

        IngredientVO vo =
                new IngredientVO();

        vo.setOriginal(text);
        vo.setName(text);
        vo.setAmount(null);
        vo.setUnit(null);
        vo.setAmountText(null);

        return vo;
    }


    /**
     * "20", "2.5", "1/2", "1⅓" 등을 Double로 변환
     */
    private static Double convertNumber(
            String value) {

        if (value == null) {
            return null;
        }

        value = value.trim();

        try {

            // 1/2
            if (value.contains("/")) {

                String[] arr =
                        value.split("/");

                double a =
                        Double.parseDouble(arr[0]);

                double b =
                        Double.parseDouble(arr[1]);

                return a / b;
            }

            // 1⅓
            if (value.contains("⅓")) {

                String number =
                        value.replace("⅓", "");

                return Double.parseDouble(number)
                        + (1.0 / 3.0);
            }

            // 1⅔
            if (value.contains("⅔")) {

                String number =
                        value.replace("⅔", "");

                return Double.parseDouble(number)
                        + (2.0 / 3.0);
            }

            // 1½
            if (value.contains("½")) {

                String number =
                        value.replace("½", "");

                return Double.parseDouble(number)
                        + 0.5;
            }

            // 1¼
            if (value.contains("¼")) {

                String number =
                        value.replace("¼", "");

                return Double.parseDouble(number)
                        + 0.25;
            }

            // 1¾
            if (value.contains("¾")) {

                String number =
                        value.replace("¾", "");

                return Double.parseDouble(number)
                        + 0.75;
            }

            return Double.parseDouble(value);

        } catch (Exception e) {

            return null;
        }
    }


    /**
     * 단독 소제목인지 확인
     */
    private static boolean isSectionTitle(
            String text) {

        return text.equals("고명")
                || text.equals("육수")
                || text.equals("양념")
                || text.equals("양념장")
                || text.equals("필수재료")
                || text.equals("재료");
    }


    /**
     * "필수재료 : 통조림 햄..."
     * "양념장 : 고춧가루..."
     *
     * 앞의 소제목 제거
     */
    private static String removeSectionTitle(
            String text) {

        return text.replaceFirst(
                "^(필수재료|재료|육수|양념장|양념)"
                + "\\s*[:：]\\s*",
                ""
        ).trim();
    }


    /**
     * 첫 줄이 레시피 제목인지 판단
     */
    private static boolean isRecipeTitle(
            String text) {

        // 숫자 + 단위가 있으면 재료일 가능성이 높음
        if (text.matches(".*\\d+.*")) {
            return false;
        }

        // 약간 등의 표현이 있으면 재료
        if (text.matches(
                ".*(약간|적당량|조금).*")) {
            return false;
        }

        // 소제목
        if (isSectionTitle(text)) {
            return false;
        }

        return true;
    }
}