package utilities;

import java.util.Comparator;

public class IntComparator implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
        Integer int1 = (Integer)o1;
        Integer int2 = (Integer)o2;
        return int1.compareTo(int2);
    }

    public boolean equals(Object o2) {
        return this.equals(o2);
    }
}
