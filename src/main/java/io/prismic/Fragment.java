package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public interface Fragment {

  public static class StructuredText implements Fragment {

    public static interface Block {

      public static interface Text extends Block {
        public String getText();
        public List<Span> getSpans();
      }

      public static class Heading implements Text {
        private final String text;
        private final List<Span> spans;
        private final int level;

        public Heading(String text, List<Span> spans, int level) {
          this.text = text;
          this.spans = spans;
          this.level = level;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

        public int getLevel() {
          return level;
        }

      }

      public static class Paragraph implements Text {
        private final String text;
        private final List<Span> spans;
        
        public Paragraph(String text, List<Span> spans) {
          this.text = text;
          this.spans = spans;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

      }

      public static class ListItem implements Text {
        private final String text;
        private final List<Span> spans;
        private final boolean ordered;

        public ListItem(String text, List<Span> spans, boolean ordered) {
          this.text = text;
          this.spans = spans;
          this.ordered = ordered;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

        public boolean isOrdered() {
          return ordered;
        }

      }

    }

    public static interface Span {
      public int getStart();
      public int getEnd();

      public static class Em implements Span {
        private final int start;
        private final int end;

        public Em(int start, int end) {
          this.start = start;
          this.end = end;
        }

        public int getStart() {
          return start;
        }

        public int getEnd() {
          return end;
        }

      }

      public static class Strong implements Span {
        private final int start;
        private final int end;

        public Strong(int start, int end) {
          this.start = start;
          this.end = end;
        }

        public int getStart() {
          return start;
        }

        public int getEnd() {
          return end;
        }

      }

    }

    // --

    final List<Block> blocks;

    public StructuredText(List<Block> blocks) {
      this.blocks = blocks;
    }

    public List<Block> getBlocks() {
      return blocks;
    }

    // --

    static Span parseSpan(JsonNode json) {
      String type = json.path("type").asText();
      int start = json.path("start").intValue();
      int end = json.path("end").intValue();
      JsonNode data = json.with("data");

      if("strong".equals(type)) {
        return new Span.Strong(start, end);
      }

      if("em".equals(type)) {
        return new Span.Em(start, end);
      }

      return null;
    }

    static Object[] parseText(JsonNode json) {
      String text = json.path("text").asText();
      List<Span> spans = new ArrayList<Span>();
      for(JsonNode spanJson: json.withArray("spans")) {
        Span span = parseSpan(spanJson);
        if(span != null) {
          spans.add(span);
        }
      }
      return new Object[] { text, spans };
    }

    static Block parseBlock(JsonNode json) {
      String type = json.path("type").asText();

      if("heading1".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 1);
      }

      if("heading2".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 2);
      }

      if("heading3".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 3);
      }

      if("heading4".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 4);
      }

      if("paragraph".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Paragraph((String)p[0], (List<Span>)p[1]);
      }

      if("list-item".equals(type)) {
        Object[] p = parseText(json);
        return new Block.ListItem((String)p[0], (List<Span>)p[1], false);
      }

      return null;
    }

    static StructuredText parse(JsonNode json) {
      List<Block> blocks = new ArrayList<Block>();
      for(JsonNode blockJson: json) {
        Block block = parseBlock(blockJson);
        if(block != null) {
          blocks.add(block);
        }
      }
      return new StructuredText(blocks);
    }

  }

}