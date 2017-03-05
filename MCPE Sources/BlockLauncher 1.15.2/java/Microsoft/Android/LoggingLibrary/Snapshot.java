package Microsoft.Android.LoggingLibrary;

import Ms.Telemetry.CllHeartBeat;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.IJsonSerializable;
import java.io.IOException;
import java.io.Writer;

public class Snapshot extends Data<CllHeartBeat> implements IJsonSerializable {
    public Snapshot() {
        InitializeFields();
        SetupAttributes();
    }

    protected void InitializeFields() {
        this.QualifiedName = "Microsoft.Android.LoggingLibrary.Snapshot";
    }

    public void SetupAttributes() {
        this.Attributes.put("Description", "Android's Client Telemetry Snapshot");
    }

    protected String serializeContent(Writer writer) throws IOException {
        return super.serializeContent(writer);
    }
}
