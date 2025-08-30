
package com.dinidu.lexceylon.Utils;

import com.dinidu.lexceylon.R;

public class PromptBuilder {

    public static String build(String questionSi) {
        // Strict JSON-only prompt per your schema
        String schema = "{\n" +
                "  \"version\": \"1.0\",\n" +
                "  \"question_si\": \"\",\n" +
                "  \"relevant_laws\": [\n" +
                "    {\"act\":\"\", \"year\":\"\", \"sections\":[{\"number\":\"\", \"title\":\"\", \"summary\":\"\"}], \"confidence\": 0.0}\n" +
                "  ],\n" +
                "  \"advice\": {\"summary\":\"\", \"steps\": [\"\"], \"warnings\": [\"\"]},\n" +
                "  \"where_to_file\": [\n" +
                "    {\"authority\":\"\", \"office_level\":\"\", \"how_to_file\":\"\", \"fee\":\"\", \"deadline\":\"\", \"documents\":[\"\"], \"contact\":\"\", \"location\":\"\", \"online_url\":\"\"}\n" +
                "  ],\n" +
                "  \"disclaimer\":\"\"\n" +
                "}";

        String rules = "ඔබ ශ්‍රී ලංකා නීති පිළිබඳ Sinhala legal assistant එකකි.\n" +
                "පහත JSON schema එකට අනුකූලව JSON පමණක් පිළිතුරු දෙන්න. Markdown/අමතර පෙළ/කේත වළකු.\n" +
                "සියලුම අගයන් Sinhala වලින් දෙන්න. දන්නා නොමිලිත තොරතුරු තිබේ නම් \"තහවුරු කළ යුතුයි\" ලෙස සඳහන් කරන්න.\n" +
                "නීති නාමය/අංකය/වගන්තිය එළිදරව් කරන්න. advice.steps අංකිත/තරමක් කෙටි පියවර වලින් දෙන්න.\n" +
                "where_to_file තුළ authority/office_level/how_to_file/fee/deadline/documents/contact/location/online_url පුරවන්න.\n" +
                "අවසන්වීම් ලෙස නීතිමය උපදෙස් නොවන බව disclaimer එකේ සඳහන් කරන්න.\n";

        return rules + "\nJSON schema:\n" + schema + "\n\nUser question (Sinhala):\n" + questionSi;
    }
}
