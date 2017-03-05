package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.Modifier;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.SchemaDef;
import com.microsoft.bond.StructDef;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class cloud extends Extension {
    private String deploymentUnit;
    private String environment;
    private String location;
    private String name;
    private String role;
    private String roleInstance;
    private String roleVer;

    public static class Schema {
        private static final Metadata deploymentUnit_metadata = new Metadata();
        private static final Metadata environment_metadata = new Metadata();
        private static final Metadata location_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        private static final Metadata name_metadata = new Metadata();
        private static final Metadata roleInstance_metadata = new Metadata();
        private static final Metadata roleVer_metadata = new Metadata();
        private static final Metadata role_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("cloud");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.cloud");
            metadata.getAttributes().put("Description", "Describes the service related fields populated by the cloud service.");
            name_metadata.setName("name");
            name_metadata.setModifier(Modifier.Required);
            name_metadata.getAttributes().put("Description", "Name of the service.");
            role_metadata.setName("role");
            role_metadata.setModifier(Modifier.Required);
            role_metadata.getAttributes().put("Description", "Service role.");
            roleInstance_metadata.setName("roleInstance");
            roleInstance_metadata.setModifier(Modifier.Required);
            roleInstance_metadata.getAttributes().put("Description", "Instance id of the deployed role instance generating the event.");
            location_metadata.setName("location");
            location_metadata.setModifier(Modifier.Required);
            location_metadata.getAttributes().put("Description", "Deployed location of the role instance (canonical name of datacenter, e.g. 'East US')");
            roleVer_metadata.setName("roleVer");
            roleVer_metadata.getAttributes().put("Description", "Build version of the role. Recommended formats are either semantic version, or NT style: <MajorVersion>.<MinorVersion>.<Optional MileStone?>, <BuildNumber>.<Architecture>.<Branch>.<yyMMdd-hhmm>, e.g. 130.0.4590.3525.amd64fre.rd_fabric_n.140618-1229.");
            environment_metadata.setName("environment");
            environment_metadata.getAttributes().put("Description", "Service deployment environment or topology (e.g. Prod, PPE, ChinaProd).");
            deploymentUnit_metadata.setName("deploymentUnit");
            deploymentUnit_metadata.getAttributes().put("Description", "Service deployment or scale unit (for partitioned services).");
            schemaDef.setRoot(getTypeDef(schemaDef));
        }

        private static short getStructDef(SchemaDef schemaDef) {
            short s = (short) 0;
            while (s < schemaDef.getStructs().size()) {
                if (((StructDef) schemaDef.getStructs().get(s)).getMetadata() == metadata) {
                    break;
                }
                s = (short) (s + 1);
            }
            StructDef structDef = new StructDef();
            schemaDef.getStructs().add(structDef);
            structDef.setMetadata(metadata);
            structDef.setBase_def(Microsoft.Telemetry.Extension.Schema.getTypeDef(schemaDef));
            FieldDef fieldDef = new FieldDef();
            fieldDef.setId((short) 10);
            fieldDef.setMetadata(name_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 20);
            fieldDef.setMetadata(role_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 30);
            fieldDef.setMetadata(roleInstance_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 40);
            fieldDef.setMetadata(location_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 50);
            fieldDef.setMetadata(roleVer_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 60);
            fieldDef.setMetadata(environment_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            fieldDef = new FieldDef();
            fieldDef.setId((short) 70);
            fieldDef.setMetadata(deploymentUnit_metadata);
            fieldDef.getType().setId(BondDataType.BT_STRING);
            structDef.getFields().add(fieldDef);
            return s;
        }

        public static TypeDef getTypeDef(SchemaDef schemaDef) {
            TypeDef typeDef = new TypeDef();
            typeDef.setId(BondDataType.BT_STRUCT);
            typeDef.setStruct_def(getStructDef(schemaDef));
            return typeDef;
        }
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public BondSerializable clone() {
        return null;
    }

    public BondMirror createInstance(StructDef structDef) {
        return null;
    }

    public final String getDeploymentUnit() {
        return this.deploymentUnit;
    }

    public final String getEnvironment() {
        return this.environment;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                return this.name;
            case Token.URSH /*20*/:
                return this.role;
            case Token.NEW /*30*/:
                return this.roleInstance;
            case Token.NUMBER /*40*/:
                return this.location;
            case Token.THROW /*50*/:
                return this.roleVer;
            case Token.ENUM_INIT_ARRAY /*60*/:
                return this.environment;
            case Token.DEL_REF /*70*/:
                return this.deploymentUnit;
            default:
                return null;
        }
    }

    public final String getLocation() {
        return this.location;
    }

    public final String getName() {
        return this.name;
    }

    public final String getRole() {
        return this.role;
    }

    public final String getRoleInstance() {
        return this.roleInstance;
    }

    public final String getRoleVer() {
        return this.roleVer;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public void marshal(ProtocolWriter protocolWriter) throws IOException {
        Marshaler.marshal(this, protocolWriter);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        cloud Microsoft_Telemetry_Extensions_cloud = (cloud) obj;
        return memberwiseCompareQuick(Microsoft_Telemetry_Extensions_cloud) && memberwiseCompareDeep(Microsoft_Telemetry_Extensions_cloud);
    }

    protected boolean memberwiseCompareDeep(cloud Microsoft_Telemetry_Extensions_cloud) {
        boolean z = (super.memberwiseCompareDeep(Microsoft_Telemetry_Extensions_cloud)) && (this.name == null || this.name.equals(Microsoft_Telemetry_Extensions_cloud.name));
        z = z && (this.role == null || this.role.equals(Microsoft_Telemetry_Extensions_cloud.role));
        z = z && (this.roleInstance == null || this.roleInstance.equals(Microsoft_Telemetry_Extensions_cloud.roleInstance));
        z = z && (this.location == null || this.location.equals(Microsoft_Telemetry_Extensions_cloud.location));
        z = z && (this.roleVer == null || this.roleVer.equals(Microsoft_Telemetry_Extensions_cloud.roleVer));
        z = z && (this.environment == null || this.environment.equals(Microsoft_Telemetry_Extensions_cloud.environment));
        return z && (this.deploymentUnit == null || this.deploymentUnit.equals(Microsoft_Telemetry_Extensions_cloud.deploymentUnit));
    }

    protected boolean memberwiseCompareQuick(cloud Microsoft_Telemetry_Extensions_cloud) {
        boolean z;
        if (super.memberwiseCompareQuick(Microsoft_Telemetry_Extensions_cloud)) {
            if ((this.name == null) == (Microsoft_Telemetry_Extensions_cloud.name == null)) {
                z = true;
                z = z && (this.name == null || this.name.length() == Microsoft_Telemetry_Extensions_cloud.name.length());
                if (z) {
                    if ((this.role != null) == (Microsoft_Telemetry_Extensions_cloud.role != null)) {
                        z = true;
                        z = z && (this.role == null || this.role.length() == Microsoft_Telemetry_Extensions_cloud.role.length());
                        if (z) {
                            if ((this.roleInstance != null) == (Microsoft_Telemetry_Extensions_cloud.roleInstance != null)) {
                                z = true;
                                z = z && (this.roleInstance == null || this.roleInstance.length() == Microsoft_Telemetry_Extensions_cloud.roleInstance.length());
                                if (z) {
                                    if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                                        z = true;
                                        z = z && (this.location == null || this.location.length() == Microsoft_Telemetry_Extensions_cloud.location.length());
                                        if (z) {
                                            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                                z = true;
                                                z = z && (this.roleVer == null || this.roleVer.length() == Microsoft_Telemetry_Extensions_cloud.roleVer.length());
                                                if (z) {
                                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                                        z = true;
                                                        z = z && (this.environment == null || this.environment.length() == Microsoft_Telemetry_Extensions_cloud.environment.length());
                                                        if (z) {
                                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                                z = true;
                                                                return z && (this.deploymentUnit == null || this.deploymentUnit.length() == Microsoft_Telemetry_Extensions_cloud.deploymentUnit.length());
                                                            }
                                                        }
                                                        z = false;
                                                        if (!z) {
                                                        }
                                                    }
                                                }
                                                z = false;
                                                if (!z) {
                                                }
                                                if (z) {
                                                    if (this.deploymentUnit != null) {
                                                    }
                                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                                    }
                                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                        z = true;
                                                        if (z) {
                                                        }
                                                    }
                                                }
                                                z = false;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (!z) {
                                        }
                                        if (z) {
                                            if (this.environment != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                            }
                                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                                if (z) {
                                                    if (this.deploymentUnit != null) {
                                                    }
                                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                                    }
                                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                        z = true;
                                                        if (z) {
                                                        }
                                                    }
                                                }
                                                z = false;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (!z) {
                                }
                                if (z) {
                                    if (this.roleVer != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                                    }
                                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.environment != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                            }
                                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                                if (z) {
                                                    if (this.deploymentUnit != null) {
                                                    }
                                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                                    }
                                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                        z = true;
                                                        if (z) {
                                                        }
                                                    }
                                                }
                                                z = false;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (!z) {
                        }
                        if (z) {
                            if (this.location != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.location != null) {
                            }
                            if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.roleVer != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                                    }
                                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.environment != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                            }
                                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                                if (z) {
                                                    if (this.deploymentUnit != null) {
                                                    }
                                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                                    }
                                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                        z = true;
                                                        if (z) {
                                                        }
                                                    }
                                                }
                                                z = false;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.roleVer != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                            }
                            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (!z) {
                }
                if (z) {
                    if (this.roleInstance != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.roleInstance != null) {
                    }
                    if ((this.roleInstance != null) == (Microsoft_Telemetry_Extensions_cloud.roleInstance != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.location != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.location != null) {
                            }
                            if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.roleVer != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                                    }
                                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.environment != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                            }
                                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                                if (z) {
                                                    if (this.deploymentUnit != null) {
                                                    }
                                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                                    }
                                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                        z = true;
                                                        if (z) {
                                                        }
                                                    }
                                                }
                                                z = false;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.roleVer != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                            }
                            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.location != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.location != null) {
                    }
                    if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.roleVer != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                            }
                            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.roleVer != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                    }
                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.environment != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                    }
                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.deploymentUnit != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                    }
                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (!z) {
        }
        if (z) {
            if (this.role != null) {
            }
            if (Microsoft_Telemetry_Extensions_cloud.role != null) {
            }
            if ((this.role != null) == (Microsoft_Telemetry_Extensions_cloud.role != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.roleInstance != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.roleInstance != null) {
                    }
                    if ((this.roleInstance != null) == (Microsoft_Telemetry_Extensions_cloud.roleInstance != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.location != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.location != null) {
                            }
                            if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.roleVer != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                                    }
                                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.environment != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                            }
                                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                                if (z) {
                                                    if (this.deploymentUnit != null) {
                                                    }
                                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                                    }
                                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                        z = true;
                                                        if (z) {
                                                        }
                                                    }
                                                }
                                                z = false;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.roleVer != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                            }
                            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.location != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.location != null) {
                    }
                    if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.roleVer != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                            }
                            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.roleVer != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                    }
                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.environment != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                    }
                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.deploymentUnit != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                    }
                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.roleInstance != null) {
            }
            if (Microsoft_Telemetry_Extensions_cloud.roleInstance != null) {
            }
            if ((this.roleInstance != null) == (Microsoft_Telemetry_Extensions_cloud.roleInstance != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.location != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.location != null) {
                    }
                    if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.roleVer != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                            }
                            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.environment != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                                    }
                                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                        if (z) {
                                            if (this.deploymentUnit != null) {
                                            }
                                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                            }
                                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                                z = true;
                                                if (z) {
                                                }
                                            }
                                        }
                                        z = false;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.roleVer != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                    }
                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.environment != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                    }
                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.deploymentUnit != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                    }
                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.location != null) {
            }
            if (Microsoft_Telemetry_Extensions_cloud.location != null) {
            }
            if ((this.location != null) == (Microsoft_Telemetry_Extensions_cloud.location != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.roleVer != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
                    }
                    if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.environment != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                            }
                            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                                z = true;
                                if (z) {
                                }
                                if (z) {
                                    if (this.deploymentUnit != null) {
                                    }
                                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                                    }
                                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                        z = true;
                                        if (z) {
                                        }
                                    }
                                }
                                z = false;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.environment != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                    }
                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.deploymentUnit != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                    }
                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.roleVer != null) {
            }
            if (Microsoft_Telemetry_Extensions_cloud.roleVer != null) {
            }
            if ((this.roleVer != null) == (Microsoft_Telemetry_Extensions_cloud.roleVer != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.environment != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
                    }
                    if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                        z = true;
                        if (z) {
                        }
                        if (z) {
                            if (this.deploymentUnit != null) {
                            }
                            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                            }
                            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                                z = true;
                                if (z) {
                                }
                            }
                        }
                        z = false;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
                if (z) {
                    if (this.deploymentUnit != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                    }
                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.environment != null) {
            }
            if (Microsoft_Telemetry_Extensions_cloud.environment != null) {
            }
            if ((this.environment != null) == (Microsoft_Telemetry_Extensions_cloud.environment != null)) {
                z = true;
                if (z) {
                }
                if (z) {
                    if (this.deploymentUnit != null) {
                    }
                    if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
                    }
                    if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                        z = true;
                        if (z) {
                        }
                    }
                }
                z = false;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
        if (z) {
            if (this.deploymentUnit != null) {
            }
            if (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null) {
            }
            if ((this.deploymentUnit != null) == (Microsoft_Telemetry_Extensions_cloud.deploymentUnit != null)) {
                z = true;
                if (z) {
                }
            }
        }
        z = false;
        if (z) {
        }
    }

    public void read(ProtocolReader protocolReader) throws IOException {
        protocolReader.readBegin();
        readNested(protocolReader);
        protocolReader.readEnd();
    }

    public void read(ProtocolReader protocolReader, BondSerializable bondSerializable) throws IOException {
    }

    public void readNested(ProtocolReader protocolReader) throws IOException {
        if (!protocolReader.hasCapability(ProtocolCapability.TAGGED)) {
            readUntagged(protocolReader, false);
        } else if (readTagged(protocolReader, false)) {
            ReadHelper.skipPartialStruct(protocolReader);
        }
    }

    protected boolean readTagged(ProtocolReader protocolReader, boolean z) throws IOException {
        boolean z2 = false;
        protocolReader.readStructBegin(z);
        if (super.readTagged(protocolReader, true)) {
            while (true) {
                FieldTag readFieldBegin = protocolReader.readFieldBegin();
                if (readFieldBegin.type == BondDataType.BT_STOP || readFieldBegin.type == BondDataType.BT_STOP_BASE) {
                    if (readFieldBegin.type == BondDataType.BT_STOP_BASE) {
                        z2 = true;
                    }
                    protocolReader.readStructEnd();
                } else {
                    switch (readFieldBegin.id) {
                        case Token.BITXOR /*10*/:
                            this.name = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.URSH /*20*/:
                            this.role = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NEW /*30*/:
                            this.roleInstance = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.NUMBER /*40*/:
                            this.location = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.THROW /*50*/:
                            this.roleVer = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.ENUM_INIT_ARRAY /*60*/:
                            this.environment = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        case Token.DEL_REF /*70*/:
                            this.deploymentUnit = ReadHelper.readString(protocolReader, readFieldBegin.type);
                            break;
                        default:
                            protocolReader.skip(readFieldBegin.type);
                            break;
                    }
                    protocolReader.readFieldEnd();
                }
            }
        }
        return z2;
    }

    protected void readUntagged(ProtocolReader protocolReader, boolean z) throws IOException {
        boolean hasCapability = protocolReader.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolReader.readStructBegin(z);
        super.readUntagged(protocolReader, true);
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.name = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.role = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.roleInstance = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.location = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.roleVer = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.environment = protocolReader.readString();
        }
        if (!(hasCapability && protocolReader.readFieldOmitted())) {
            this.deploymentUnit = protocolReader.readString();
        }
        protocolReader.readStructEnd();
    }

    public void reset() {
        reset("cloud", "Microsoft.Telemetry.Extensions.cloud");
    }

    protected void reset(String str, String str2) {
        super.reset(str, str2);
        this.name = BuildConfig.FLAVOR;
        this.role = BuildConfig.FLAVOR;
        this.roleInstance = BuildConfig.FLAVOR;
        this.location = BuildConfig.FLAVOR;
        this.roleVer = BuildConfig.FLAVOR;
        this.environment = BuildConfig.FLAVOR;
        this.deploymentUnit = BuildConfig.FLAVOR;
    }

    public final void setDeploymentUnit(String str) {
        this.deploymentUnit = str;
    }

    public final void setEnvironment(String str) {
        this.environment = str;
    }

    public void setField(FieldDef fieldDef, Object obj) {
        switch (fieldDef.getId()) {
            case Token.BITXOR /*10*/:
                this.name = (String) obj;
                return;
            case Token.URSH /*20*/:
                this.role = (String) obj;
                return;
            case Token.NEW /*30*/:
                this.roleInstance = (String) obj;
                return;
            case Token.NUMBER /*40*/:
                this.location = (String) obj;
                return;
            case Token.THROW /*50*/:
                this.roleVer = (String) obj;
                return;
            case Token.ENUM_INIT_ARRAY /*60*/:
                this.environment = (String) obj;
                return;
            case Token.DEL_REF /*70*/:
                this.deploymentUnit = (String) obj;
                return;
            default:
                return;
        }
    }

    public final void setLocation(String str) {
        this.location = str;
    }

    public final void setName(String str) {
        this.name = str;
    }

    public final void setRole(String str) {
        this.role = str;
    }

    public final void setRoleInstance(String str) {
        this.roleInstance = str;
    }

    public final void setRoleVer(String str) {
        this.roleVer = str;
    }

    public void unmarshal(InputStream inputStream) throws IOException {
        Marshaler.unmarshal(inputStream, this);
    }

    public void unmarshal(InputStream inputStream, BondSerializable bondSerializable) throws IOException {
        Marshaler.unmarshal(inputStream, (SchemaDef) bondSerializable, this);
    }

    public void write(ProtocolWriter protocolWriter) throws IOException {
        protocolWriter.writeBegin();
        ProtocolWriter firstPassWriter = protocolWriter.getFirstPassWriter();
        if (firstPassWriter != null) {
            writeNested(firstPassWriter, false);
            writeNested(protocolWriter, false);
        } else {
            writeNested(protocolWriter, false);
        }
        protocolWriter.writeEnd();
    }

    public void writeNested(ProtocolWriter protocolWriter, boolean z) throws IOException {
        boolean hasCapability = protocolWriter.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        protocolWriter.writeStructBegin(Schema.metadata, z);
        super.writeNested(protocolWriter, true);
        protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.name_metadata);
        protocolWriter.writeString(this.name);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.role_metadata);
        protocolWriter.writeString(this.role);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.roleInstance_metadata);
        protocolWriter.writeString(this.roleInstance);
        protocolWriter.writeFieldEnd();
        protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 40, Schema.location_metadata);
        protocolWriter.writeString(this.location);
        protocolWriter.writeFieldEnd();
        if (hasCapability && this.roleVer == Schema.roleVer_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 50, Schema.roleVer_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 50, Schema.roleVer_metadata);
            protocolWriter.writeString(this.roleVer);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.environment == Schema.environment_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 60, Schema.environment_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 60, Schema.environment_metadata);
            protocolWriter.writeString(this.environment);
            protocolWriter.writeFieldEnd();
        }
        if (hasCapability && this.deploymentUnit == Schema.deploymentUnit_metadata.getDefault_value().getString_value()) {
            protocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 70, Schema.deploymentUnit_metadata);
        } else {
            protocolWriter.writeFieldBegin(BondDataType.BT_STRING, 70, Schema.deploymentUnit_metadata);
            protocolWriter.writeString(this.deploymentUnit);
            protocolWriter.writeFieldEnd();
        }
        protocolWriter.writeStructEnd(z);
    }
}
