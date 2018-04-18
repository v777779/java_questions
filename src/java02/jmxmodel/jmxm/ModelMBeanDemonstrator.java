package java02.jmxmodel.jmxm;

import javax.management.*;
import javax.management.modelmbean.*;
import java.lang.management.ManagementFactory;

public class ModelMBeanDemonstrator {
    private MBeanServer mbeanServer;

    public ModelMBeanDemonstrator() {
        this.mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    private ModelMBeanOperationInfo[] buildModelMBeanOperationInfo() {
        final MBeanParameterInfo augendParameter =
                new MBeanParameterInfo(
                        "augend",
                        Integer.TYPE.toString(),
                        "The first parameter in the addition (augend).");

        final MBeanParameterInfo addendParameter =
                new MBeanParameterInfo(
                        "addend",
                        Integer.TYPE.toString(),
                        "The second parameter in the addition (addend).");

        final ModelMBeanOperationInfo addOperationInfo =
                new ModelMBeanOperationInfo(
                        "add",
                        "Integer Addition",
                        new MBeanParameterInfo[]{augendParameter, addendParameter},
                        Integer.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO);


        final MBeanParameterInfo minuendParameter =
                new MBeanParameterInfo(
                        "minuend",
                        Integer.TYPE.toString(),
                        "The first parameter in the substraction (minuend).");

        final MBeanParameterInfo subtrahendParameter =
                new MBeanParameterInfo(
                        "subtrahend",
                        Integer.TYPE.toString(),
                        "The second parameter in the subtraction (subtrahend).");

        final ModelMBeanOperationInfo subtractOperationInfo =
                new ModelMBeanOperationInfo(
                        "subtract",
                        "Integer Subtraction",
                        new MBeanParameterInfo[]{minuendParameter, subtrahendParameter},
                        Integer.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO);

        final MBeanParameterInfo factorOneParameter =
                new MBeanParameterInfo(
                        "factor1",
                        Integer.TYPE.toString(),
                        "The first factor in the multiplication.");

        final MBeanParameterInfo factorTwoParameter =
                new MBeanParameterInfo(
                        "factor2",
                        Integer.TYPE.toString(),
                        "The second factor in the multiplication.");

        final ModelMBeanOperationInfo multiplyOperationInfo =
                new ModelMBeanOperationInfo(
                        "multiply",
                        "Integer Multiplication",
                        new MBeanParameterInfo[]{factorOneParameter, factorTwoParameter},
                        Integer.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO);

        final MBeanParameterInfo dividendParameter =
                new MBeanParameterInfo(
                        "dividend",
                        Integer.TYPE.toString(),
                        "The dividend in the division.");

        final MBeanParameterInfo divisorParameter =
                new MBeanParameterInfo(
                        "divisor",
                        Integer.TYPE.toString(),
                        "The divisor in the division.");

        final ModelMBeanOperationInfo divideOperationInfo =
                new ModelMBeanOperationInfo(
                        "divide",
                        "Integer Division",
                        new MBeanParameterInfo[]{dividendParameter, divisorParameter},
                        Double.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO);

        return new ModelMBeanOperationInfo[]
                {addOperationInfo, subtractOperationInfo,
                        multiplyOperationInfo, divideOperationInfo};
    }

    private Descriptor buildDescriptor() {
        final Descriptor descriptor = new DescriptorSupport();
        descriptor.setField("name", "ModelMBeanInTheRaw");
        descriptor.setField("descriptorType", "mbean");
        return descriptor;
    }

    private void setModelMBeanManagedResource(
            final ModelMBean modelMBeanToManageResource) {
        try {
            modelMBeanToManageResource.setManagedResource(new SimpleCalculator(), "ObjectReference");

        } catch (RuntimeOperationsException | InstanceNotFoundException |
                InvalidTargetObjectTypeException | MBeanException e) {
            e.printStackTrace();
        }
    }

    private ModelMBean createRawModelMBean() {
        RequiredModelMBean modelmbean = null;
        try {
            final ModelMBeanInfoSupport modelMBeanInfo =
                    new ModelMBeanInfoSupport(
                            SimpleCalculator.class.getName(),
                            "A simple integer calculator.",
                            null,                                 // attributes
                            null,                               // constructors
                            buildModelMBeanOperationInfo(),
                            null,                              // notifications
                            buildDescriptor());
            modelmbean = new RequiredModelMBean(modelMBeanInfo);
            setModelMBeanManagedResource(modelmbean);
        } catch (MBeanException e) {
            e.printStackTrace();
        }

        return modelmbean;
    }

    private void registerModelMBean(
            final ModelMBean modelMBean,
            final String objectNameString) {
        try {
            final ObjectName objectName = new ObjectName(objectNameString);
            this.mbeanServer.registerMBean(modelMBean, objectName);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    public void createAndRegisterRawModelMBean() {
        final String rawModelMBeanObjectNameString = "modelmbean:type=raw";
        registerModelMBean(createRawModelMBean(), rawModelMBeanObjectNameString);
    }

    public static void pause(final int millisecondsToPause) {
        try {
            Thread.sleep(millisecondsToPause);
        } catch (InterruptedException threadAwakened) {
            System.err.println("Don't wake me up!\n" + threadAwakened.getMessage());
        }
    }

    public static void main(String[] args) {
        final ModelMBeanDemonstrator me = new ModelMBeanDemonstrator();
        me.createAndRegisterRawModelMBean();
        pause(1000000);
    }
}