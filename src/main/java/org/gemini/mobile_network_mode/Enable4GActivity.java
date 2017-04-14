package org.gemini.mobile_network_mode;

public class Enable4GActivity extends MobileNetworkActivity
{
    protected int[] networkTypes()
    {
        return new int[]
            { 9, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 8, 10, 11 };
    }
}
