package org.example.spring.create;

import static org.example.spring.SuHanApplication.BEANDEFINITION_MAP;
import static org.example.spring.SuHanApplication.SINGLETONBEAN_MAP;
import static org.example.spring.create.CreatBeans.creatSingletonBean;
import static org.example.spring.create.InjectingBeans.injectingBean;

public class Creates {
    public static void creat(){

        //实例化
        creatSingletonBean(BEANDEFINITION_MAP);

        //依赖注入
        injectingBean(SINGLETONBEAN_MAP);


    }
}
