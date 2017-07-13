import org.openjdk.jol.info.GraphLayout;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static java.lang.System.out;

public class MethodHandleFootprint {

    public static void main(String[] args) throws Throwable {
        System.out.println(
                "WARNING: Make sure you are running with a non-moving collector like Epsilon GC, " +
                        "otherwise footprint differences are unreliable."
        );

        MethodHandle m1_1 = MethodHandles.lookup().findVirtual(MethodHandleFootprint.class, "m1", MethodType.methodType(void.class));
        MethodHandle m1_2 = MethodHandles.lookup().findVirtual(MethodHandleFootprint.class, "m1", MethodType.methodType(void.class));
        MethodHandle m2   = MethodHandles.lookup().findVirtual(MethodHandleFootprint.class, "m2", MethodType.methodType(void.class));

        System.out.println("=== On creation: ");
        System.out.println();
        test(m1_1, m1_2, m2);

        MethodHandleFootprint t = new MethodHandleFootprint();
        m1_1.invoke(t);
        m1_2.invoke(t);
        m2.invoke(t);

        System.out.println("=== After first invoke: ");
        System.out.println();
        test(m1_1, m1_2, m2);

        for (int c = 0; c < 1000000; c++) {
            m1_1.invoke(t);
            m1_2.invoke(t);
            m2.invoke(t);
        }

        System.out.println("=== After lots of invokes: ");
        System.out.println();
        test(m1_1, m1_2, m2);
    }

    private static void test(MethodHandle m1_1, MethodHandle m1_2, MethodHandle m2) {
        GraphLayout gl1_1 = GraphLayout.parseInstance(m1_1);
        GraphLayout gl1_2 = GraphLayout.parseInstance(m1_2);
        GraphLayout gl2   = GraphLayout.parseInstance(m2);

        GraphLayout diff_same = gl1_2.subtract(gl1_1);
        GraphLayout diff_diff = gl2.subtract(gl1_1);

        out.println("Single MH:");
        out.println(gl1_1.toFootprint());

        out.println("Difference between MHs pointing to same method:");
        out.println(diff_same.toFootprint());

        out.println("Difference between MHs pointing to diff methods:");
        out.println(diff_diff.toFootprint());
    }

    public void m1() {}
    public void m2() {}

}
