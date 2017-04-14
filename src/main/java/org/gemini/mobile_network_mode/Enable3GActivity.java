package org.gemini.mobile_network_mode;

public class Enable3GActivity extends MobileNetworkActivity
{
    protected int[] networkTypes()
    {
        return new int[] { 3, 0, 7, 4, 5, 6, 2 };
    }
}
