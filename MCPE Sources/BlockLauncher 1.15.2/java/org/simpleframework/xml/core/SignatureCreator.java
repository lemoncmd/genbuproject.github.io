package org.simpleframework.xml.core;

import com.microsoft.cll.android.EventEnums;
import java.util.List;

class SignatureCreator implements Creator {
    private final List<Parameter> list;
    private final Signature signature;
    private final Class type;

    public SignatureCreator(Signature signature) {
        this.type = signature.getType();
        this.list = signature.getAll();
        this.signature = signature;
    }

    private double getAdjustment(double d) {
        return d > 0.0d ? (((double) this.list.size()) / 1000.0d) + (d / ((double) this.list.size())) : d / ((double) this.list.size());
    }

    private double getPercentage(Criteria criteria) throws Exception {
        double d = 0.0d;
        for (Parameter parameter : this.list) {
            if (criteria.get(parameter.getKey()) != null) {
                d = 1.0d + d;
            } else if (parameter.isRequired()) {
                return EventEnums.SampleRate_Unspecified;
            } else {
                if (parameter.isPrimitive()) {
                    return EventEnums.SampleRate_Unspecified;
                }
            }
        }
        return getAdjustment(d);
    }

    private Object getVariable(Criteria criteria, int i) throws Exception {
        Variable remove = criteria.remove(((Parameter) this.list.get(i)).getKey());
        return remove != null ? remove.getValue() : null;
    }

    public Object getInstance() throws Exception {
        return this.signature.create();
    }

    public Object getInstance(Criteria criteria) throws Exception {
        Object[] toArray = this.list.toArray();
        for (int i = 0; i < this.list.size(); i++) {
            toArray[i] = getVariable(criteria, i);
        }
        return this.signature.create(toArray);
    }

    public double getScore(Criteria criteria) throws Exception {
        Signature copy = this.signature.copy();
        for (Object next : criteria) {
            Parameter parameter = copy.get(next);
            Variable variable = criteria.get(next);
            Contact contact = variable.getContact();
            if (parameter != null && !Support.isAssignable(variable.getValue().getClass(), parameter.getType())) {
                return EventEnums.SampleRate_Unspecified;
            }
            if (contact.isReadOnly() && parameter == null) {
                return EventEnums.SampleRate_Unspecified;
            }
        }
        return getPercentage(criteria);
    }

    public Signature getSignature() {
        return this.signature;
    }

    public Class getType() {
        return this.type;
    }

    public String toString() {
        return this.signature.toString();
    }
}
