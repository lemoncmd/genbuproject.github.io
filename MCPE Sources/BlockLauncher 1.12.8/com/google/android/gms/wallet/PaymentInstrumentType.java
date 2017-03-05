package com.google.android.gms.wallet;

import java.util.ArrayList;
import java.util.Arrays;

public final class PaymentInstrumentType
{
  public static final int AMEX = 3;
  public static final int CHROME_PROXY_CARD_TYPE = 2;
  public static final int DISCOVER = 4;
  public static final int JCB = 5;
  public static final int MASTER_CARD = 2;
  public static final int VISA = 1;
  
  public static ArrayList<Integer> getAll()
  {
    return new ArrayList(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5) }));
  }
}


/* Location:              C:\Users\Genbu Hase\ドキュメント\Genbu\Tool\Programing\Jad\BlockLauncher 1.12.8.jar!\com\google\android\gms\wallet\PaymentInstrumentType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */