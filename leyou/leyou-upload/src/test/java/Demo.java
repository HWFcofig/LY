import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @author admin
 * @ClassName Demo
 * @date 2020/4/4
 * @Version 1.0
 **/
public class Demo {

    @Test
    public void demoT(){
        List l = new ArrayList();
        l.add(1);
        l.add(1);
        l.add(2);
        l.add(2);
        l.add(5);
        HashSet h = new HashSet(l);
        l.clear();
        l.add(h);
        l.forEach(lq->{
            System.out.println(lq);
        });
        System.out.println(l);
    }
}
