package echowand.info;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.info.PropertyConstraintHumidity;

public class HumiditySensorInfo extends DeviceObjectInfo {

	public HumiditySensorInfo() {
        setClassEOJ(new ClassEOJ((byte)0x00, (byte)(0x12)));
        
        add(EPC.xE0, true, false, false, 1, new PropertyConstraintHumidity());
    }

}
