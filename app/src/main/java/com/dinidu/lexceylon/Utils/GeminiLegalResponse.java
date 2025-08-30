
package com.dinidu.lexceylon.Utils;

import java.util.List;

public class GeminiLegalResponse {
    public String version;
    public String question_si;
    public List<RelevantLaw> relevant_laws;
    public Advice advice;
    public List<WhereToFile> where_to_file;
    public String disclaimer;

    public static class RelevantLaw {
        public String act;
        public String year;
        public List<Section> sections;
        public double confidence;
    }
    public static class Section {
        public String number;
        public String title;
        public String summary;
    }
    public static class Advice {
        public String summary;
        public List<String> steps;
        public List<String> warnings;
    }
    public static class WhereToFile {
        public String authority;
        public String office_level;
        public String how_to_file;
        public String fee;
        public String deadline;
        public List<String> documents;
        public String contact;
        public String location;
        public String online_url;
    }
}
