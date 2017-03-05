package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.List;

class ClassInstantiator implements Instantiator {
    private final List<Creator> creators;
    private final Detail detail;
    private final Creator primary;
    private final ParameterMap registry;

    public ClassInstantiator(List<Creator> list, Creator creator, ParameterMap parameterMap, Detail detail) {
        this.creators = list;
        this.registry = parameterMap;
        this.primary = creator;
        this.detail = detail;
    }

    private Creator getCreator(Criteria criteria) throws Exception {
        Creator creator = this.primary;
        Creator creator2 = creator;
        double d = 0.0d;
        for (Creator creator3 : this.creators) {
            double score = creator3.getScore(criteria);
            if (score > d) {
                d = score;
                creator2 = creator3;
            }
        }
        return creator2;
    }

    public List<Creator> getCreators() {
        return new ArrayList(this.creators);
    }

    public Object getInstance() throws Exception {
        return this.primary.getInstance();
    }

    public Object getInstance(Criteria criteria) throws Exception {
        Creator creator = getCreator(criteria);
        if (creator != null) {
            return creator.getInstance(criteria);
        }
        throw new PersistenceException("Constructor not matched for %s", this.detail);
    }

    public Parameter getParameter(String str) {
        return (Parameter) this.registry.get(str);
    }

    public List<Parameter> getParameters() {
        return this.registry.getAll();
    }

    public boolean isDefault() {
        return this.creators.size() <= 1 && this.primary != null;
    }

    public String toString() {
        return String.format("creator for %s", new Object[]{this.detail});
    }
}
