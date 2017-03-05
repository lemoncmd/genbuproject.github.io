package com.microsoft.onlineid.internal;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import com.microsoft.onlineid.RequestOptions;
import java.util.List;

public class Intents {

    public static class DataBuilder {
        private final Builder _builder = new Builder();

        public DataBuilder() {
            this._builder.scheme("extras");
        }

        public DataBuilder add(RequestOptions requestOptions) {
            this._builder.appendPath("options");
            if (requestOptions == null) {
                this._builder.appendPath("null");
                return this;
            }
            Bundle asBundle = requestOptions.asBundle();
            if (asBundle != null) {
                return add(asBundle.toString());
            }
            this._builder.appendPath("empty");
            return this;
        }

        public DataBuilder add(String str) {
            this._builder.appendPath("str").appendPath(str);
            return this;
        }

        public DataBuilder add(List<String> list) {
            this._builder.appendPath("list");
            if (list != null) {
                for (String appendPath : list) {
                    this._builder.appendPath(appendPath);
                }
            } else {
                this._builder.appendPath("null");
            }
            return this;
        }

        public Uri build() {
            return this._builder.build();
        }
    }
}
