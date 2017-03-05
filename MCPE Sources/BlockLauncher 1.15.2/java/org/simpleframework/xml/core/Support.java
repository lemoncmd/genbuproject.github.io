package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.filter.Filter;
import org.simpleframework.xml.filter.PlatformFilter;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Style;
import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;
import org.simpleframework.xml.transform.Transformer;

class Support implements Filter {
    private final DetailExtractor defaults;
    private final DetailExtractor details;
    private final Filter filter;
    private final Format format;
    private final InstanceFactory instances;
    private final LabelExtractor labels;
    private final Matcher matcher;
    private final ScannerFactory scanners;
    private final Transformer transform;

    public Support() {
        this(new PlatformFilter());
    }

    public Support(Filter filter) {
        this(filter, new EmptyMatcher());
    }

    public Support(Filter filter, Matcher matcher) {
        this(filter, matcher, new Format());
    }

    public Support(Filter filter, Matcher matcher, Format format) {
        this.defaults = new DetailExtractor(this, DefaultType.FIELD);
        this.transform = new Transformer(matcher);
        this.scanners = new ScannerFactory(this);
        this.details = new DetailExtractor(this);
        this.labels = new LabelExtractor(format);
        this.instances = new InstanceFactory();
        this.matcher = matcher;
        this.filter = filter;
        this.format = format;
    }

    private String getClassName(Class cls) throws Exception {
        if (cls.isArray()) {
            cls = cls.getComponentType();
        }
        String simpleName = cls.getSimpleName();
        return cls.isPrimitive() ? simpleName : Reflector.getName(simpleName);
    }

    public static Class getPrimitive(Class cls) {
        return cls == Double.TYPE ? Double.class : cls == Float.TYPE ? Float.class : cls == Integer.TYPE ? Integer.class : cls == Long.TYPE ? Long.class : cls == Boolean.TYPE ? Boolean.class : cls == Character.TYPE ? Character.class : cls == Short.TYPE ? Short.class : cls == Byte.TYPE ? Byte.class : cls;
    }

    public static boolean isAssignable(Class cls, Class cls2) {
        if (cls.isPrimitive()) {
            cls = getPrimitive(cls);
        }
        if (cls2.isPrimitive()) {
            cls2 = getPrimitive(cls2);
        }
        return cls2.isAssignableFrom(cls);
    }

    public static boolean isFloat(Class cls) throws Exception {
        return cls == Double.class || cls == Float.class || cls == Float.TYPE || cls == Double.TYPE;
    }

    public Detail getDetail(Class cls) {
        return getDetail(cls, null);
    }

    public Detail getDetail(Class cls, DefaultType defaultType) {
        return defaultType != null ? this.defaults.getDetail(cls) : this.details.getDetail(cls);
    }

    public ContactList getFields(Class cls) throws Exception {
        return getFields(cls, null);
    }

    public ContactList getFields(Class cls, DefaultType defaultType) throws Exception {
        return defaultType != null ? this.defaults.getFields(cls) : this.details.getFields(cls);
    }

    public Format getFormat() {
        return this.format;
    }

    public Instance getInstance(Class cls) {
        return this.instances.getInstance(cls);
    }

    public Instance getInstance(Value value) {
        return this.instances.getInstance(value);
    }

    public Label getLabel(Contact contact, Annotation annotation) throws Exception {
        return this.labels.getLabel(contact, annotation);
    }

    public List<Label> getLabels(Contact contact, Annotation annotation) throws Exception {
        return this.labels.getList(contact, annotation);
    }

    public ContactList getMethods(Class cls) throws Exception {
        return getMethods(cls, null);
    }

    public ContactList getMethods(Class cls, DefaultType defaultType) throws Exception {
        return defaultType != null ? this.defaults.getMethods(cls) : this.details.getMethods(cls);
    }

    public String getName(Class cls) throws Exception {
        String name = getScanner(cls).getName();
        return name != null ? name : getClassName(cls);
    }

    public Scanner getScanner(Class cls) throws Exception {
        return this.scanners.getInstance(cls);
    }

    public Style getStyle() {
        return this.format.getStyle();
    }

    public Transform getTransform(Class cls) throws Exception {
        return this.matcher.match(cls);
    }

    public boolean isContainer(Class cls) {
        return (Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls)) ? true : cls.isArray();
    }

    public boolean isPrimitive(Class cls) throws Exception {
        return (cls == String.class || cls == Float.class || cls == Double.class || cls == Long.class || cls == Integer.class || cls == Boolean.class || cls.isEnum() || cls.isPrimitive()) ? true : this.transform.valid(cls);
    }

    public Object read(String str, Class cls) throws Exception {
        return this.transform.read(str, cls);
    }

    public String replace(String str) {
        return this.filter.replace(str);
    }

    public boolean valid(Class cls) throws Exception {
        return this.transform.valid(cls);
    }

    public String write(Object obj, Class cls) throws Exception {
        return this.transform.write(obj, cls);
    }
}
