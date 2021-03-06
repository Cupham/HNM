package echowand.object;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.info.ObjectInfo;
import echowand.info.PropertyInfo;
import echowand.util.Constraint;

public class LocalObject implements EchonetObject {
    private static final Logger logger = Logger.getLogger(LocalObject.class.getName());
    private static final String className = LocalObject.class.getName();
    
    private EOJ eoj;
    private ObjectInfo objectInfo;
    private EnumMap<EPC, ObjectData> propertyData;
    private LinkedList<LocalObjectDelegate> delegates;

    public LocalObject(ObjectInfo objectInfo) {
        logger.entering(className, "LocalObject", objectInfo);
        
        this.objectInfo = objectInfo;
        this.eoj = objectInfo.getClassEOJ().getEOJWithInstanceCode((byte)0x01);
        propertyData = new EnumMap<EPC, ObjectData>(EPC.class);
        delegates = new LinkedList<LocalObjectDelegate>();
        
        int len = objectInfo.size();
        for (int i=0; i<len; i++) {
            PropertyInfo info = objectInfo.getAtIndex(i);
            propertyData.put(info.epc, new ObjectData(info.initialData));
        }
        
        logger.exiting(className, "LocalObject");
    }
    
    private synchronized LinkedList<LocalObjectDelegate> cloneDelegates() {
        return new LinkedList<LocalObjectDelegate>(delegates);
    }

    public void setInstanceCode(byte instanceCode) {
        logger.entering(className, "setInstanceCode", instanceCode);
        
        this.eoj = eoj.getClassEOJ().getEOJWithInstanceCode(instanceCode);
        
        logger.exiting(className, "setInstanceCode");
    }

    public synchronized boolean setInternalData(EPC epc, ObjectData data) {
        logger.entering(className, "setInternalData", new Object[]{epc, data});
        
        if (!contains(epc)) {
            logger.exiting(className, "setInternalData", false);
            return false;
        }

        propertyData.put(epc, data);
        
        logger.exiting(className, "setInternalData", true);
        return true;
    }

    public boolean forceSetData(EPC epc, ObjectData data) {
        logger.entering(className, "forceSetData", new Object[]{epc, data});
        
        ObjectData oldData = this.getData(epc);

        LocalObjectDelegate.SetState result = setDataDelegate(epc, data, oldData);

        if (result.isFail()) {
            logger.exiting(className, "forceSetData", false);
            return false;
        }
        
        setInternalData(epc, result.getNewData());
        
        if (result.isDataChanged()) {
            notifyDataChanged(epc, result.getNewData(), result.getCurrentData());
        }

        logger.exiting(className, "forceSetData", true);
        return true;
    }

    @Override
    public boolean setData(EPC epc, ObjectData data) {
        logger.entering(className, "setData", new Object[]{epc, data});
        
        if (!this.isSettable(epc)) {
            logger.exiting(className, "setData", false);
            return false;
        }

        PropertyInfo propertyInfo = objectInfo.get(epc);
        Constraint constraint = propertyInfo.constraint;
        if (!constraint.isValid(data.toBytes())) {
            logger.exiting(className, "setData", false);
            return false;
        }
        
        boolean ret = forceSetData(epc, data);
        logger.exiting(className, "setData", ret);
        return ret;
    }

    public synchronized ObjectData getInternalData(EPC epc) {
        logger.entering(className, "getInternalData", epc);
        
        ObjectData data = propertyData.get(epc);
        
        logger.exiting(className, "getInternalData", data);
        
        return data;
    }

    public ObjectData forceGetData(EPC epc) {
        logger.entering(className, "forceGetData", epc);
        
        LocalObjectDelegate.GetState result = getDataDelegate(epc);
        
        if (result.isFail()) {
            logger.exiting(className, "forceGetData", null);
            return null;
        }

        logger.exiting(className, "forceGetData", result.getGetData());
        return result.getGetData();
    }

    @Override
    public ObjectData getData(EPC epc) {
        logger.entering(className, "getData", epc);
        
        if (!this.isGettable(epc)) {
            logger.exiting(className, "getData", null);
            return null;
        }

        ObjectData data = forceGetData(epc);
        
        logger.exiting(className, "getData", data);
        return data;
    }

    @Override
    public EOJ getEOJ() {
        return eoj;
    }

    @Override
    public synchronized boolean contains(EPC epc) {
        return propertyData.containsKey(epc);
    }

    @Override
    public boolean isGettable(EPC epc) {
        return objectInfo.get(epc).gettable;
    }

    @Override
    public boolean isSettable(EPC epc) {
        return objectInfo.get(epc).settable;
    }

    @Override
    public boolean isObservable(EPC epc) {
        return objectInfo.get(epc).observable;
    }
    
    private void logMessages(LocalObjectDelegate.State state) {
        int count = state.countMessages();
        for (int i=0; i<count; i++) {
            logger.info(state.getMessage(i));
        }
    }

    public boolean notifyDataChanged(EPC epc, ObjectData curData, ObjectData oldData) {
        logger.entering(className, "notifyDataChanged", new Object[]{epc, curData, oldData});
        
        LocalObjectDelegate.NotifyState result = new LocalObjectDelegate.NotifyState();
        for (LocalObjectDelegate delegate: cloneDelegates()) {
            delegate.notifyDataChanged(result, this, epc, curData, oldData);
            if (result.isDone()) {
                break;
            }
        }
        
        logMessages(result);
        
        logger.exiting(className, "notifyDataChanged", result.isFail());
        return result.isFail();
    }
    
    private LocalObjectDelegate.SetState setDataDelegate(EPC epc, ObjectData newData, ObjectData curData) {
        logger.entering(className, "setDataDelegate", new Object[]{epc, newData, curData});
        
        LocalObjectDelegate.SetState result = new LocalObjectDelegate.SetState(newData, curData);
        for (LocalObjectDelegate delegate: cloneDelegates()) {
            delegate.setData(result, this, epc, newData, curData);
            if (result.isDone()) {
                break;
            }
        }
        
        logMessages(result);
        
        logger.exiting(className, "setDataDelegate", result);
        return result;
    }
    
    private LocalObjectDelegate.GetState getDataDelegate(EPC epc) {
        logger.entering(className, "getDataDelegate", new Object[]{epc});
        
        LocalObjectDelegate.GetState result = new LocalObjectDelegate.GetState(this.getInternalData(epc));
        for (LocalObjectDelegate delegate: cloneDelegates()) {
            delegate.getData(result, this, epc);
            if (result.isDone()) {
                break;
            }
        }
        
        logMessages(result);
        
        logger.exiting(className, "getDataDelegate", result);
        return result;
    }
    
    public synchronized int countDelegates() {
        logger.entering(className, "countDelegates");
        
        int count = delegates.size();
        
        logger.entering(className, "countDelegates", count);
        return count;
    }

    public synchronized boolean addDelegate(LocalObjectDelegate delegate) {
        logger.entering(className, "addDelegate", delegate);
        
        boolean result;
        
        if (delegates.contains(delegate)) {
            result = false;
        } else {
            result = delegates.add(delegate);
        }
        
        logger.entering(className, "addDelegate", result);
        return result;
    }

    public synchronized boolean removeDelegate(LocalObjectDelegate delegate) {
        logger.entering(className, "removeDelegate", delegate);
        
        boolean result = delegates.remove(delegate);
        
        logger.entering(className, "removeDelegate", result);
        return result;
    }
    
    public synchronized LocalObjectDelegate getDelegate(int index) {
        logger.entering(className, "getDelegate", index);
        
        LocalObjectDelegate delegate = delegates.get(index);
        
        logger.entering(className, "getDelegate", delegate);
        return delegate;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{EOJ: " + eoj + "}";
    }
}
