package echowand.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.logging.Logger;

import echowand.common.ClassEOJ;
import echowand.common.EPC;
import echowand.common.PropertyMap;
import echowand.util.Constraint;
/*
 * @author HiepNguyen
 */
public class BasicObjectInfo implements ObjectInfo {
	private static final Logger logger = Logger.getLogger(DeviceObjectInfo.class.getName());
    private static final String className = DeviceObjectInfo.class.getName();
    
    private static class PropertyInfoComparator implements Comparator<PropertyInfo>, Serializable {
        @Override
        public int compare(PropertyInfo p1, PropertyInfo p2) {
            int c1 = (p1.epc.toByte() & 0x000000ff);
            int c2 = (p2.epc.toByte() & 0x000000ff);
            return c1 - c2;
        }
    }
    
    private ClassEOJ classEOJ;
    private TreeSet<PropertyInfo> props = new TreeSet<PropertyInfo>(new BasicObjectInfo.PropertyInfoComparator());
    private ArrayList<PropertyInfo> propList = null;
    private boolean needsUpdatePropertyMap = true;

    private synchronized void updatePropertyMapsInNeeds() {
        logger.entering(className, "updatePropertyMapsInNeeds");
        
        if (!needsUpdatePropertyMap) {
            logger.exiting(className, "updatePropertyMapsInNeeds");
            return;
        }

        needsUpdatePropertyMap = false;

        PropertyMap annoMap = new PropertyMap();
        PropertyMap setMap = new PropertyMap();
        PropertyMap getMap = new PropertyMap();
        int count = size();
        for (int i = 0; i < count; i++) {
            PropertyInfo info = getAtIndex(i);
            if (info.gettable) {
                getMap.set(info.epc);
            }
            if (info.settable) {
                setMap.set(info.epc);
            }
            if (info.observable) {
                annoMap.set(info.epc);
            }
        }
        
        PropertyInfo annoInfo = get(EPC.x9D);
        addWithoutUpdating(annoInfo.epc, annoInfo.gettable, annoInfo.settable, annoInfo.observable, annoMap.toBytes());
        PropertyInfo setInfo = get(EPC.x9E);
        addWithoutUpdating(setInfo.epc, setInfo.gettable, setInfo.settable, setInfo.observable, setMap.toBytes());
        PropertyInfo getInfo = get(EPC.x9F);
        addWithoutUpdating(getInfo.epc, getInfo.gettable, getInfo.settable, getInfo.observable, getMap.toBytes());

        makePropListOutdated();

        logger.exiting(className, "updatePropertyMapsInNeeds");
    }

    private synchronized void makeUpdatePropertyMapNeeded() {
        logger.entering(className, "makeUpdatePropertyMapNeeded");
        
        needsUpdatePropertyMap = true;
        
        logger.exiting(className, "makeUpdatePropertyMapNeeded");
    }
    
    @Override
    public ClassEOJ getClassEOJ() {
        return classEOJ;
    }
    
    public final void setClassEOJ(ClassEOJ ceoj) {
        logger.entering(className, "setClassEOJ", ceoj);
        
        classEOJ = ceoj;
        
        logger.exiting(className, "setClassEOJ");
    }
    
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, int size) {
        return add(new PropertyInfo(epc, gettable, settable, observable, size));
    }
    
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, int size, Constraint constraint) {
        return add(new PropertyInfo(epc, gettable, settable, observable, size, constraint));
    }
    
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data) {
        return add(new PropertyInfo(epc, gettable, settable, observable, data));
    }
    
    public final boolean add(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data, Constraint constraint) {
        return add(new PropertyInfo(epc, gettable, settable, observable, data, constraint));
    }
    
    public final boolean add(PropertyInfo prop) {
        logger.entering(className, "add", prop);
        
        boolean status = addWithoutUpdating(prop);
        makeUpdatePropertyMapNeeded();
        makePropListOutdated();
        
        logger.exiting(className, "add", status);
        return status;
    }
    
    public final boolean remove(EPC epc) {
        logger.entering(className, "remove", epc);
        
        boolean status = props.remove(get(epc));
        makeUpdatePropertyMapNeeded();
        makePropListOutdated();
        
        logger.exiting(className, "remove", status);
        return status;
    }
    
    private boolean addWithoutUpdating(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data) {
        return addWithoutUpdating(new PropertyInfo(epc, gettable, settable, observable, data));
    }
    
    private boolean addWithoutUpdating(PropertyInfo prop) {
        logger.entering(className, "addWithoutUpdating", prop);
        
        props.remove(prop);
        boolean status = props.add(prop);
        
        logger.exiting(className, "addWithoutUpdating", status);
        return status;
    }
    
    private void makePropListOutdated() {
        logger.entering(className, "makePropListOutdated");
        
        propList = null;
        
        logger.exiting(className, "makePropListOutdated");
    }
    
    private ArrayList<PropertyInfo> getPropList() {
        logger.entering(className, "getPropList");
        
        if (propList == null) {
            propList = new ArrayList<PropertyInfo>(props);
        }
        
        logger.exiting(className, "getPropList", propList);
        return propList;
    }
    
    @Override
    public PropertyInfo getAtIndex(int index) {
        logger.entering(className, "getAtIndex", index);
        
        updatePropertyMapsInNeeds();
        PropertyInfo propertyInfo = getPropList().get(index);
        
        logger.exiting(className, "getAtIndex", propertyInfo);
        return propertyInfo;
    }
    
    @Override
    public PropertyInfo get(EPC epc) {
        logger.entering(className, "get", epc);

        updatePropertyMapsInNeeds();
        for (PropertyInfo prop : getPropList()) {
            if (prop.epc == epc) {
                logger.exiting(className, "get", prop);
                return prop;
            }
        }

        PropertyInfo propertyInfo = new PropertyInfo(epc, false, false, false, 0);
        logger.exiting(className, "get", propertyInfo);
        return propertyInfo;
    }
    
    @Override
    public final int size() {
        return getPropList().size();
    }
}
