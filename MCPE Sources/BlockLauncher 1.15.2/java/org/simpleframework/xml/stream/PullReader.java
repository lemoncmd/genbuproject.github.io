package org.simpleframework.xml.stream;

import org.xmlpull.v1.XmlPullParser;

class PullReader implements EventReader {
    private XmlPullParser parser;
    private EventNode peek;

    private static class End extends EventToken {
        private End() {
        }

        public boolean isEnd() {
            return true;
        }
    }

    private static class Entry extends EventAttribute {
        private final String name;
        private final String prefix;
        private final String reference;
        private final XmlPullParser source;
        private final String value;

        public Entry(XmlPullParser xmlPullParser, int i) {
            this.reference = xmlPullParser.getAttributeNamespace(i);
            this.prefix = xmlPullParser.getAttributePrefix(i);
            this.value = xmlPullParser.getAttributeValue(i);
            this.name = xmlPullParser.getAttributeName(i);
            this.source = xmlPullParser;
        }

        public String getName() {
            return this.name;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getReference() {
            return this.reference;
        }

        public Object getSource() {
            return this.source;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isReserved() {
            return false;
        }
    }

    private static class Start extends EventElement {
        private final int line;
        private final String name;
        private final String prefix;
        private final String reference;
        private final XmlPullParser source;

        public Start(XmlPullParser xmlPullParser) {
            this.reference = xmlPullParser.getNamespace();
            this.line = xmlPullParser.getLineNumber();
            this.prefix = xmlPullParser.getPrefix();
            this.name = xmlPullParser.getName();
            this.source = xmlPullParser;
        }

        public int getLine() {
            return this.line;
        }

        public String getName() {
            return this.name;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getReference() {
            return this.reference;
        }

        public Object getSource() {
            return this.source;
        }
    }

    private static class Text extends EventToken {
        private final XmlPullParser source;
        private final String text;

        public Text(XmlPullParser xmlPullParser) {
            this.text = xmlPullParser.getText();
            this.source = xmlPullParser;
        }

        public Object getSource() {
            return this.source;
        }

        public String getValue() {
            return this.text;
        }

        public boolean isText() {
            return true;
        }
    }

    public PullReader(XmlPullParser xmlPullParser) {
        this.parser = xmlPullParser;
    }

    private Entry attribute(int i) throws Exception {
        return new Entry(this.parser, i);
    }

    private Start build(Start start) throws Exception {
        int attributeCount = this.parser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            Entry attribute = attribute(i);
            if (!attribute.isReserved()) {
                start.add(attribute);
            }
        }
        return start;
    }

    private End end() throws Exception {
        return new End();
    }

    private EventNode read() throws Exception {
        int next = this.parser.next();
        return next != 1 ? next == 2 ? start() : next == 4 ? text() : next == 3 ? end() : read() : null;
    }

    private Start start() throws Exception {
        Start start = new Start(this.parser);
        return start.isEmpty() ? build(start) : start;
    }

    private Text text() throws Exception {
        return new Text(this.parser);
    }

    public EventNode next() throws Exception {
        EventNode eventNode = this.peek;
        if (eventNode == null) {
            return read();
        }
        this.peek = null;
        return eventNode;
    }

    public EventNode peek() throws Exception {
        if (this.peek == null) {
            this.peek = next();
        }
        return this.peek;
    }
}
