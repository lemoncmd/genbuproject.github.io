package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;

class ModelList extends ArrayList<Model> {
    public ModelList build() {
        ModelList modelList = new ModelList();
        Iterator it = iterator();
        while (it.hasNext()) {
            modelList.register((Model) it.next());
        }
        return modelList;
    }

    public boolean isEmpty() {
        Iterator it = iterator();
        while (it.hasNext()) {
            Model model = (Model) it.next();
            if (model != null && !model.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Model lookup(int i) {
        return i <= size() ? (Model) get(i - 1) : null;
    }

    public void register(Model model) {
        int index = model.getIndex();
        int size = size();
        for (int i = 0; i < index; i++) {
            if (i >= size) {
                add(null);
            }
            if (i == index - 1) {
                set(index - 1, model);
            }
        }
    }

    public Model take() {
        while (!isEmpty()) {
            Model model = (Model) remove(0);
            if (!model.isEmpty()) {
                return model;
            }
        }
        return null;
    }
}
