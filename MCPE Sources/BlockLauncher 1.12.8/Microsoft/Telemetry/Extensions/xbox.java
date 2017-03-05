package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
import Microsoft.Telemetry.Extension.Schema;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.SchemaDef;
import com.microsoft.bond.StructDef;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.Variant;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class xbox
  extends Extension
{
  private String deviceType;
  private String eventSequence;
  private String expiryTimestamp;
  private String isDevelopmentAccount;
  private String isTestAccount;
  private String issueTimestamp;
  private String sandboxId;
  private String signedInUsers;
  private String sti;
  private String titleId;
  private String xblDeviceId;
  
  public static SchemaDef getRuntimeSchema()
  {
    return Schema.schemaDef;
  }
  
  public BondSerializable clone()
  {
    return null;
  }
  
  public BondMirror createInstance(StructDef paramStructDef)
  {
    return null;
  }
  
  public final String getDeviceType()
  {
    return this.deviceType;
  }
  
  public final String getEventSequence()
  {
    return this.eventSequence;
  }
  
  public final String getExpiryTimestamp()
  {
    return this.expiryTimestamp;
  }
  
  public Object getField(FieldDef paramFieldDef)
  {
    switch (paramFieldDef.getId())
    {
    default: 
      return null;
    case 10: 
      return this.sti;
    case 20: 
      return this.eventSequence;
    case 30: 
      return this.issueTimestamp;
    case 40: 
      return this.expiryTimestamp;
    case 50: 
      return this.sandboxId;
    case 60: 
      return this.deviceType;
    case 70: 
      return this.xblDeviceId;
    case 80: 
      return this.signedInUsers;
    case 90: 
      return this.isDevelopmentAccount;
    case 100: 
      return this.isTestAccount;
    }
    return this.titleId;
  }
  
  public final String getIsDevelopmentAccount()
  {
    return this.isDevelopmentAccount;
  }
  
  public final String getIsTestAccount()
  {
    return this.isTestAccount;
  }
  
  public final String getIssueTimestamp()
  {
    return this.issueTimestamp;
  }
  
  public final String getSandboxId()
  {
    return this.sandboxId;
  }
  
  public SchemaDef getSchema()
  {
    return getRuntimeSchema();
  }
  
  public final String getSignedInUsers()
  {
    return this.signedInUsers;
  }
  
  public final String getSti()
  {
    return this.sti;
  }
  
  public final String getTitleId()
  {
    return this.titleId;
  }
  
  public final String getXblDeviceId()
  {
    return this.xblDeviceId;
  }
  
  public void marshal(ProtocolWriter paramProtocolWriter)
    throws IOException
  {
    Marshaler.marshal(this, paramProtocolWriter);
  }
  
  public boolean memberwiseCompare(Object paramObject)
  {
    if (paramObject == null) {}
    do
    {
      return false;
      paramObject = (xbox)paramObject;
    } while ((!memberwiseCompareQuick((xbox)paramObject)) || (!memberwiseCompareDeep((xbox)paramObject)));
    return true;
  }
  
  protected boolean memberwiseCompareDeep(xbox paramxbox)
  {
    int i;
    if (super.memberwiseCompareDeep(paramxbox))
    {
      i = 1;
      if (i == 0) {
        break label172;
      }
      if (this.sti != null) {
        break label158;
      }
      label21:
      i = 1;
      label23:
      if (i == 0) {
        break label191;
      }
      if (this.eventSequence != null) {
        break label177;
      }
      label34:
      i = 1;
      label36:
      if (i == 0) {
        break label210;
      }
      if (this.issueTimestamp != null) {
        break label196;
      }
      label47:
      i = 1;
      label49:
      if (i == 0) {
        break label229;
      }
      if (this.expiryTimestamp != null) {
        break label215;
      }
      label60:
      i = 1;
      label62:
      if (i == 0) {
        break label248;
      }
      if (this.sandboxId != null) {
        break label234;
      }
      label73:
      i = 1;
      label75:
      if (i == 0) {
        break label267;
      }
      if (this.deviceType != null) {
        break label253;
      }
      label86:
      i = 1;
      label88:
      if (i == 0) {
        break label286;
      }
      if (this.xblDeviceId != null) {
        break label272;
      }
      label99:
      i = 1;
      label101:
      if (i == 0) {
        break label305;
      }
      if (this.signedInUsers != null) {
        break label291;
      }
      label112:
      i = 1;
      label114:
      if (i == 0) {
        break label324;
      }
      if (this.isDevelopmentAccount != null) {
        break label310;
      }
      label125:
      i = 1;
      label127:
      if (i == 0) {
        break label343;
      }
      if (this.isTestAccount != null) {
        break label329;
      }
      label138:
      i = 1;
      label140:
      if (i == 0) {
        break label362;
      }
      if (this.titleId != null) {
        break label348;
      }
    }
    label158:
    label172:
    label177:
    label191:
    label196:
    label210:
    label215:
    label229:
    label234:
    label248:
    label253:
    label267:
    label272:
    label286:
    label291:
    label305:
    label310:
    label324:
    label329:
    label343:
    label348:
    while (this.titleId.equals(paramxbox.titleId))
    {
      return true;
      i = 0;
      break;
      if (this.sti.equals(paramxbox.sti)) {
        break label21;
      }
      i = 0;
      break label23;
      if (this.eventSequence.equals(paramxbox.eventSequence)) {
        break label34;
      }
      i = 0;
      break label36;
      if (this.issueTimestamp.equals(paramxbox.issueTimestamp)) {
        break label47;
      }
      i = 0;
      break label49;
      if (this.expiryTimestamp.equals(paramxbox.expiryTimestamp)) {
        break label60;
      }
      i = 0;
      break label62;
      if (this.sandboxId.equals(paramxbox.sandboxId)) {
        break label73;
      }
      i = 0;
      break label75;
      if (this.deviceType.equals(paramxbox.deviceType)) {
        break label86;
      }
      i = 0;
      break label88;
      if (this.xblDeviceId.equals(paramxbox.xblDeviceId)) {
        break label99;
      }
      i = 0;
      break label101;
      if (this.signedInUsers.equals(paramxbox.signedInUsers)) {
        break label112;
      }
      i = 0;
      break label114;
      if (this.isDevelopmentAccount.equals(paramxbox.isDevelopmentAccount)) {
        break label125;
      }
      i = 0;
      break label127;
      if (this.isTestAccount.equals(paramxbox.isTestAccount)) {
        break label138;
      }
      i = 0;
      break label140;
    }
    label362:
    return false;
  }
  
  protected boolean memberwiseCompareQuick(xbox paramxbox)
  {
    int i;
    label23:
    int j;
    if (super.memberwiseCompareQuick(paramxbox))
    {
      i = 1;
      if (i == 0) {
        break label487;
      }
      if (this.sti != null) {
        break label477;
      }
      i = 1;
      if (paramxbox.sti != null) {
        break label482;
      }
      j = 1;
      label32:
      if (i != j) {
        break label487;
      }
      i = 1;
      label39:
      if (i == 0) {
        break label509;
      }
      if (this.sti != null) {
        break label492;
      }
      label50:
      i = 1;
      label52:
      if (i == 0) {
        break label524;
      }
      if (this.eventSequence != null) {
        break label514;
      }
      i = 1;
      label65:
      if (paramxbox.eventSequence != null) {
        break label519;
      }
      j = 1;
      label74:
      if (i != j) {
        break label524;
      }
      i = 1;
      label81:
      if (i == 0) {
        break label546;
      }
      if (this.eventSequence != null) {
        break label529;
      }
      label92:
      i = 1;
      label94:
      if (i == 0) {
        break label561;
      }
      if (this.issueTimestamp != null) {
        break label551;
      }
      i = 1;
      label107:
      if (paramxbox.issueTimestamp != null) {
        break label556;
      }
      j = 1;
      label116:
      if (i != j) {
        break label561;
      }
      i = 1;
      label123:
      if (i == 0) {
        break label583;
      }
      if (this.issueTimestamp != null) {
        break label566;
      }
      label134:
      i = 1;
      label136:
      if (i == 0) {
        break label598;
      }
      if (this.expiryTimestamp != null) {
        break label588;
      }
      i = 1;
      label149:
      if (paramxbox.expiryTimestamp != null) {
        break label593;
      }
      j = 1;
      label158:
      if (i != j) {
        break label598;
      }
      i = 1;
      label165:
      if (i == 0) {
        break label620;
      }
      if (this.expiryTimestamp != null) {
        break label603;
      }
      label176:
      i = 1;
      label178:
      if (i == 0) {
        break label635;
      }
      if (this.sandboxId != null) {
        break label625;
      }
      i = 1;
      label191:
      if (paramxbox.sandboxId != null) {
        break label630;
      }
      j = 1;
      label200:
      if (i != j) {
        break label635;
      }
      i = 1;
      label207:
      if (i == 0) {
        break label657;
      }
      if (this.sandboxId != null) {
        break label640;
      }
      label218:
      i = 1;
      label220:
      if (i == 0) {
        break label672;
      }
      if (this.deviceType != null) {
        break label662;
      }
      i = 1;
      label233:
      if (paramxbox.deviceType != null) {
        break label667;
      }
      j = 1;
      label242:
      if (i != j) {
        break label672;
      }
      i = 1;
      label249:
      if (i == 0) {
        break label694;
      }
      if (this.deviceType != null) {
        break label677;
      }
      label260:
      i = 1;
      label262:
      if (i == 0) {
        break label709;
      }
      if (this.xblDeviceId != null) {
        break label699;
      }
      i = 1;
      label275:
      if (paramxbox.xblDeviceId != null) {
        break label704;
      }
      j = 1;
      label284:
      if (i != j) {
        break label709;
      }
      i = 1;
      label291:
      if (i == 0) {
        break label731;
      }
      if (this.xblDeviceId != null) {
        break label714;
      }
      label302:
      i = 1;
      label304:
      if (i == 0) {
        break label746;
      }
      if (this.signedInUsers != null) {
        break label736;
      }
      i = 1;
      label317:
      if (paramxbox.signedInUsers != null) {
        break label741;
      }
      j = 1;
      label326:
      if (i != j) {
        break label746;
      }
      i = 1;
      label333:
      if (i == 0) {
        break label768;
      }
      if (this.signedInUsers != null) {
        break label751;
      }
      label344:
      i = 1;
      label346:
      if (i == 0) {
        break label783;
      }
      if (this.isDevelopmentAccount != null) {
        break label773;
      }
      i = 1;
      label359:
      if (paramxbox.isDevelopmentAccount != null) {
        break label778;
      }
      j = 1;
      label368:
      if (i != j) {
        break label783;
      }
      i = 1;
      label375:
      if (i == 0) {
        break label805;
      }
      if (this.isDevelopmentAccount != null) {
        break label788;
      }
      label386:
      i = 1;
      label388:
      if (i == 0) {
        break label820;
      }
      if (this.isTestAccount != null) {
        break label810;
      }
      i = 1;
      label401:
      if (paramxbox.isTestAccount != null) {
        break label815;
      }
      j = 1;
      label410:
      if (i != j) {
        break label820;
      }
      i = 1;
      label417:
      if (i == 0) {
        break label842;
      }
      if (this.isTestAccount != null) {
        break label825;
      }
      label428:
      i = 1;
      label430:
      if (i == 0) {
        break label857;
      }
      if (this.titleId != null) {
        break label847;
      }
      i = 1;
      label443:
      if (paramxbox.titleId != null) {
        break label852;
      }
      j = 1;
      label452:
      if (i != j) {
        break label857;
      }
      i = 1;
      label459:
      if (i == 0) {
        break label879;
      }
      if (this.titleId != null) {
        break label862;
      }
    }
    label477:
    label482:
    label487:
    label492:
    label509:
    label514:
    label519:
    label524:
    label529:
    label546:
    label551:
    label556:
    label561:
    label566:
    label583:
    label588:
    label593:
    label598:
    label603:
    label620:
    label625:
    label630:
    label635:
    label640:
    label657:
    label662:
    label667:
    label672:
    label677:
    label694:
    label699:
    label704:
    label709:
    label714:
    label731:
    label736:
    label741:
    label746:
    label751:
    label768:
    label773:
    label778:
    label783:
    label788:
    label805:
    label810:
    label815:
    label820:
    label825:
    label842:
    label847:
    label852:
    label857:
    label862:
    while (this.titleId.length() == paramxbox.titleId.length())
    {
      return true;
      i = 0;
      break;
      i = 0;
      break label23;
      j = 0;
      break label32;
      i = 0;
      break label39;
      if (this.sti.length() == paramxbox.sti.length()) {
        break label50;
      }
      i = 0;
      break label52;
      i = 0;
      break label65;
      j = 0;
      break label74;
      i = 0;
      break label81;
      if (this.eventSequence.length() == paramxbox.eventSequence.length()) {
        break label92;
      }
      i = 0;
      break label94;
      i = 0;
      break label107;
      j = 0;
      break label116;
      i = 0;
      break label123;
      if (this.issueTimestamp.length() == paramxbox.issueTimestamp.length()) {
        break label134;
      }
      i = 0;
      break label136;
      i = 0;
      break label149;
      j = 0;
      break label158;
      i = 0;
      break label165;
      if (this.expiryTimestamp.length() == paramxbox.expiryTimestamp.length()) {
        break label176;
      }
      i = 0;
      break label178;
      i = 0;
      break label191;
      j = 0;
      break label200;
      i = 0;
      break label207;
      if (this.sandboxId.length() == paramxbox.sandboxId.length()) {
        break label218;
      }
      i = 0;
      break label220;
      i = 0;
      break label233;
      j = 0;
      break label242;
      i = 0;
      break label249;
      if (this.deviceType.length() == paramxbox.deviceType.length()) {
        break label260;
      }
      i = 0;
      break label262;
      i = 0;
      break label275;
      j = 0;
      break label284;
      i = 0;
      break label291;
      if (this.xblDeviceId.length() == paramxbox.xblDeviceId.length()) {
        break label302;
      }
      i = 0;
      break label304;
      i = 0;
      break label317;
      j = 0;
      break label326;
      i = 0;
      break label333;
      if (this.signedInUsers.length() == paramxbox.signedInUsers.length()) {
        break label344;
      }
      i = 0;
      break label346;
      i = 0;
      break label359;
      j = 0;
      break label368;
      i = 0;
      break label375;
      if (this.isDevelopmentAccount.length() == paramxbox.isDevelopmentAccount.length()) {
        break label386;
      }
      i = 0;
      break label388;
      i = 0;
      break label401;
      j = 0;
      break label410;
      i = 0;
      break label417;
      if (this.isTestAccount.length() == paramxbox.isTestAccount.length()) {
        break label428;
      }
      i = 0;
      break label430;
      i = 0;
      break label443;
      j = 0;
      break label452;
      i = 0;
      break label459;
    }
    label879:
    return false;
  }
  
  public void read(ProtocolReader paramProtocolReader)
    throws IOException
  {
    paramProtocolReader.readBegin();
    readNested(paramProtocolReader);
    paramProtocolReader.readEnd();
  }
  
  public void read(ProtocolReader paramProtocolReader, BondSerializable paramBondSerializable)
    throws IOException
  {}
  
  public void readNested(ProtocolReader paramProtocolReader)
    throws IOException
  {
    if (!paramProtocolReader.hasCapability(ProtocolCapability.TAGGED)) {
      readUntagged(paramProtocolReader, false);
    }
    while (!readTagged(paramProtocolReader, false)) {
      return;
    }
    ReadHelper.skipPartialStruct(paramProtocolReader);
  }
  
  protected boolean readTagged(ProtocolReader paramProtocolReader, boolean paramBoolean)
    throws IOException
  {
    boolean bool = false;
    paramProtocolReader.readStructBegin(paramBoolean);
    ProtocolReader.FieldTag localFieldTag;
    if (!super.readTagged(paramProtocolReader, true))
    {
      return false;
      this.sti = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
    }
    for (;;)
    {
      paramProtocolReader.readFieldEnd();
      localFieldTag = paramProtocolReader.readFieldBegin();
      if ((localFieldTag.type == BondDataType.BT_STOP) || (localFieldTag.type == BondDataType.BT_STOP_BASE))
      {
        paramBoolean = bool;
        if (localFieldTag.type == BondDataType.BT_STOP_BASE) {
          paramBoolean = true;
        }
        paramProtocolReader.readStructEnd();
        return paramBoolean;
      }
      switch (localFieldTag.id)
      {
      case 10: 
      default: 
        paramProtocolReader.skip(localFieldTag.type);
        break;
      case 20: 
        this.eventSequence = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 30: 
        this.issueTimestamp = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 40: 
        this.expiryTimestamp = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 50: 
        this.sandboxId = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 60: 
        this.deviceType = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 70: 
        this.xblDeviceId = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 80: 
        this.signedInUsers = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 90: 
        this.isDevelopmentAccount = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 100: 
        this.isTestAccount = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
        break;
      case 110: 
        this.titleId = ReadHelper.readString(paramProtocolReader, localFieldTag.type);
      }
    }
  }
  
  protected void readUntagged(ProtocolReader paramProtocolReader, boolean paramBoolean)
    throws IOException
  {
    boolean bool = paramProtocolReader.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
    paramProtocolReader.readStructBegin(paramBoolean);
    super.readUntagged(paramProtocolReader, true);
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.sti = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.eventSequence = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.issueTimestamp = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.expiryTimestamp = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.sandboxId = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.deviceType = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.xblDeviceId = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.signedInUsers = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.isDevelopmentAccount = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.isTestAccount = paramProtocolReader.readString();
    }
    if ((!bool) || (!paramProtocolReader.readFieldOmitted())) {
      this.titleId = paramProtocolReader.readString();
    }
    paramProtocolReader.readStructEnd();
  }
  
  public void reset()
  {
    reset("xbox", "Microsoft.Telemetry.Extensions.xbox");
  }
  
  protected void reset(String paramString1, String paramString2)
  {
    super.reset(paramString1, paramString2);
    this.sti = "";
    this.eventSequence = "";
    this.issueTimestamp = "";
    this.expiryTimestamp = "";
    this.sandboxId = "";
    this.deviceType = "";
    this.xblDeviceId = "";
    this.signedInUsers = "";
    this.isDevelopmentAccount = "";
    this.isTestAccount = "";
    this.titleId = "";
  }
  
  public final void setDeviceType(String paramString)
  {
    this.deviceType = paramString;
  }
  
  public final void setEventSequence(String paramString)
  {
    this.eventSequence = paramString;
  }
  
  public final void setExpiryTimestamp(String paramString)
  {
    this.expiryTimestamp = paramString;
  }
  
  public void setField(FieldDef paramFieldDef, Object paramObject)
  {
    switch (paramFieldDef.getId())
    {
    default: 
      return;
    case 10: 
      this.sti = ((String)paramObject);
      return;
    case 20: 
      this.eventSequence = ((String)paramObject);
      return;
    case 30: 
      this.issueTimestamp = ((String)paramObject);
      return;
    case 40: 
      this.expiryTimestamp = ((String)paramObject);
      return;
    case 50: 
      this.sandboxId = ((String)paramObject);
      return;
    case 60: 
      this.deviceType = ((String)paramObject);
      return;
    case 70: 
      this.xblDeviceId = ((String)paramObject);
      return;
    case 80: 
      this.signedInUsers = ((String)paramObject);
      return;
    case 90: 
      this.isDevelopmentAccount = ((String)paramObject);
      return;
    case 100: 
      this.isTestAccount = ((String)paramObject);
      return;
    }
    this.titleId = ((String)paramObject);
  }
  
  public final void setIsDevelopmentAccount(String paramString)
  {
    this.isDevelopmentAccount = paramString;
  }
  
  public final void setIsTestAccount(String paramString)
  {
    this.isTestAccount = paramString;
  }
  
  public final void setIssueTimestamp(String paramString)
  {
    this.issueTimestamp = paramString;
  }
  
  public final void setSandboxId(String paramString)
  {
    this.sandboxId = paramString;
  }
  
  public final void setSignedInUsers(String paramString)
  {
    this.signedInUsers = paramString;
  }
  
  public final void setSti(String paramString)
  {
    this.sti = paramString;
  }
  
  public final void setTitleId(String paramString)
  {
    this.titleId = paramString;
  }
  
  public final void setXblDeviceId(String paramString)
  {
    this.xblDeviceId = paramString;
  }
  
  public void unmarshal(InputStream paramInputStream)
    throws IOException
  {
    Marshaler.unmarshal(paramInputStream, this);
  }
  
  public void unmarshal(InputStream paramInputStream, BondSerializable paramBondSerializable)
    throws IOException
  {
    Marshaler.unmarshal(paramInputStream, (SchemaDef)paramBondSerializable, this);
  }
  
  public void write(ProtocolWriter paramProtocolWriter)
    throws IOException
  {
    paramProtocolWriter.writeBegin();
    ProtocolWriter localProtocolWriter = paramProtocolWriter.getFirstPassWriter();
    if (localProtocolWriter != null)
    {
      writeNested(localProtocolWriter, false);
      writeNested(paramProtocolWriter, false);
    }
    for (;;)
    {
      paramProtocolWriter.writeEnd();
      return;
      writeNested(paramProtocolWriter, false);
    }
  }
  
  public void writeNested(ProtocolWriter paramProtocolWriter, boolean paramBoolean)
    throws IOException
  {
    boolean bool = paramProtocolWriter.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
    paramProtocolWriter.writeStructBegin(Schema.metadata, paramBoolean);
    super.writeNested(paramProtocolWriter, true);
    if ((!bool) || (this.sti != Schema.sti_metadata.getDefault_value().getString_value()))
    {
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.sti_metadata);
      paramProtocolWriter.writeString(this.sti);
      paramProtocolWriter.writeFieldEnd();
      if ((bool) && (this.eventSequence == Schema.eventSequence_metadata.getDefault_value().getString_value())) {
        break label527;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.eventSequence_metadata);
      paramProtocolWriter.writeString(this.eventSequence);
      paramProtocolWriter.writeFieldEnd();
      label110:
      if ((bool) && (this.issueTimestamp == Schema.issueTimestamp_metadata.getDefault_value().getString_value())) {
        break label542;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.issueTimestamp_metadata);
      paramProtocolWriter.writeString(this.issueTimestamp);
      paramProtocolWriter.writeFieldEnd();
      label154:
      if ((bool) && (this.expiryTimestamp == Schema.expiryTimestamp_metadata.getDefault_value().getString_value())) {
        break label557;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 40, Schema.expiryTimestamp_metadata);
      paramProtocolWriter.writeString(this.expiryTimestamp);
      paramProtocolWriter.writeFieldEnd();
      label198:
      if ((bool) && (this.sandboxId == Schema.sandboxId_metadata.getDefault_value().getString_value())) {
        break label572;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 50, Schema.sandboxId_metadata);
      paramProtocolWriter.writeString(this.sandboxId);
      paramProtocolWriter.writeFieldEnd();
      label242:
      if ((bool) && (this.deviceType == Schema.deviceType_metadata.getDefault_value().getString_value())) {
        break label587;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 60, Schema.deviceType_metadata);
      paramProtocolWriter.writeString(this.deviceType);
      paramProtocolWriter.writeFieldEnd();
      label286:
      if ((bool) && (this.xblDeviceId == Schema.xblDeviceId_metadata.getDefault_value().getString_value())) {
        break label602;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 70, Schema.xblDeviceId_metadata);
      paramProtocolWriter.writeString(this.xblDeviceId);
      paramProtocolWriter.writeFieldEnd();
      label330:
      if ((bool) && (this.signedInUsers == Schema.signedInUsers_metadata.getDefault_value().getString_value())) {
        break label617;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 80, Schema.signedInUsers_metadata);
      paramProtocolWriter.writeString(this.signedInUsers);
      paramProtocolWriter.writeFieldEnd();
      label374:
      if ((bool) && (this.isDevelopmentAccount == Schema.isDevelopmentAccount_metadata.getDefault_value().getString_value())) {
        break label632;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 90, Schema.isDevelopmentAccount_metadata);
      paramProtocolWriter.writeString(this.isDevelopmentAccount);
      paramProtocolWriter.writeFieldEnd();
      label418:
      if ((bool) && (this.isTestAccount == Schema.isTestAccount_metadata.getDefault_value().getString_value())) {
        break label647;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 100, Schema.isTestAccount_metadata);
      paramProtocolWriter.writeString(this.isTestAccount);
      paramProtocolWriter.writeFieldEnd();
      label462:
      if ((bool) && (this.titleId == Schema.titleId_metadata.getDefault_value().getString_value())) {
        break label662;
      }
      paramProtocolWriter.writeFieldBegin(BondDataType.BT_STRING, 110, Schema.titleId_metadata);
      paramProtocolWriter.writeString(this.titleId);
      paramProtocolWriter.writeFieldEnd();
    }
    for (;;)
    {
      paramProtocolWriter.writeStructEnd(paramBoolean);
      return;
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.sti_metadata);
      break;
      label527:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 20, Schema.eventSequence_metadata);
      break label110;
      label542:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 30, Schema.issueTimestamp_metadata);
      break label154;
      label557:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 40, Schema.expiryTimestamp_metadata);
      break label198;
      label572:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 50, Schema.sandboxId_metadata);
      break label242;
      label587:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 60, Schema.deviceType_metadata);
      break label286;
      label602:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 70, Schema.xblDeviceId_metadata);
      break label330;
      label617:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 80, Schema.signedInUsers_metadata);
      break label374;
      label632:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 90, Schema.isDevelopmentAccount_metadata);
      break label418;
      label647:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 100, Schema.isTestAccount_metadata);
      break label462;
      label662:
      paramProtocolWriter.writeFieldOmitted(BondDataType.BT_STRING, 110, Schema.titleId_metadata);
    }
  }
  
  public static class Schema
  {
    private static final Metadata deviceType_metadata;
    private static final Metadata eventSequence_metadata;
    private static final Metadata expiryTimestamp_metadata;
    private static final Metadata isDevelopmentAccount_metadata;
    private static final Metadata isTestAccount_metadata;
    private static final Metadata issueTimestamp_metadata;
    public static final Metadata metadata = new Metadata();
    private static final Metadata sandboxId_metadata;
    public static final SchemaDef schemaDef;
    private static final Metadata signedInUsers_metadata;
    private static final Metadata sti_metadata;
    private static final Metadata titleId_metadata;
    private static final Metadata xblDeviceId_metadata;
    
    static
    {
      metadata.setName("xbox");
      metadata.setQualified_name("Microsoft.Telemetry.Extensions.xbox");
      metadata.getAttributes().put("Description", "Describes the XBox related fields and might be populated by the console.");
      sti_metadata = new Metadata();
      sti_metadata.setName("sti");
      sti_metadata.getAttributes().put("Description", "XBox supporting token index.");
      eventSequence_metadata = new Metadata();
      eventSequence_metadata.setName("eventSequence");
      eventSequence_metadata.getAttributes().put("Description", "XBox event sequence.");
      issueTimestamp_metadata = new Metadata();
      issueTimestamp_metadata.setName("issueTimestamp");
      issueTimestamp_metadata.getAttributes().put("Description", "Xbox token issue timestamp.");
      expiryTimestamp_metadata = new Metadata();
      expiryTimestamp_metadata.setName("expiryTimestamp");
      expiryTimestamp_metadata.getAttributes().put("Description", "XBox token expiry timestamp.");
      sandboxId_metadata = new Metadata();
      sandboxId_metadata.setName("sandboxId");
      sandboxId_metadata.getAttributes().put("Description", "Xbox sandboxId.");
      deviceType_metadata = new Metadata();
      deviceType_metadata.setName("deviceType");
      deviceType_metadata.getAttributes().put("Description", "XBox device type.");
      xblDeviceId_metadata = new Metadata();
      xblDeviceId_metadata.setName("xblDeviceId");
      xblDeviceId_metadata.getAttributes().put("Description", "Xbox live deviceId.");
      signedInUsers_metadata = new Metadata();
      signedInUsers_metadata.setName("signedInUsers");
      signedInUsers_metadata.getAttributes().put("Description", "XBox signed in Xuids.");
      isDevelopmentAccount_metadata = new Metadata();
      isDevelopmentAccount_metadata.setName("isDevelopmentAccount");
      isDevelopmentAccount_metadata.getAttributes().put("Description", "XBox is development account.");
      isTestAccount_metadata = new Metadata();
      isTestAccount_metadata.setName("isTestAccount");
      isTestAccount_metadata.getAttributes().put("Description", "XBox is test account.");
      titleId_metadata = new Metadata();
      titleId_metadata.setName("titleId");
      titleId_metadata.getAttributes().put("Description", "XBox titleId.");
      schemaDef = new SchemaDef();
      schemaDef.setRoot(getTypeDef(schemaDef));
    }
    
    private static short getStructDef(SchemaDef paramSchemaDef)
    {
      short s;
      for (int i = 0; i < paramSchemaDef.getStructs().size(); s = (short)(i + 1)) {
        if (((StructDef)paramSchemaDef.getStructs().get(i)).getMetadata() == metadata) {
          return i;
        }
      }
      StructDef localStructDef = new StructDef();
      paramSchemaDef.getStructs().add(localStructDef);
      localStructDef.setMetadata(metadata);
      localStructDef.setBase_def(Extension.Schema.getTypeDef(paramSchemaDef));
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)10);
      paramSchemaDef.setMetadata(sti_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)20);
      paramSchemaDef.setMetadata(eventSequence_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)30);
      paramSchemaDef.setMetadata(issueTimestamp_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)40);
      paramSchemaDef.setMetadata(expiryTimestamp_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)50);
      paramSchemaDef.setMetadata(sandboxId_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)60);
      paramSchemaDef.setMetadata(deviceType_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)70);
      paramSchemaDef.setMetadata(xblDeviceId_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)80);
      paramSchemaDef.setMetadata(signedInUsers_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)90);
      paramSchemaDef.setMetadata(isDevelopmentAccount_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)100);
      paramSchemaDef.setMetadata(isTestAccount_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      paramSchemaDef = new FieldDef();
      paramSchemaDef.setId((short)110);
      paramSchemaDef.setMetadata(titleId_metadata);
      paramSchemaDef.getType().setId(BondDataType.BT_STRING);
      localStructDef.getFields().add(paramSchemaDef);
      return s;
    }
    
    public static TypeDef getTypeDef(SchemaDef paramSchemaDef)
    {
      TypeDef localTypeDef = new TypeDef();
      localTypeDef.setId(BondDataType.BT_STRUCT);
      localTypeDef.setStruct_def(getStructDef(paramSchemaDef));
      return localTypeDef;
    }
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\Microsoft\Telemetry\Extensions\xbox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */