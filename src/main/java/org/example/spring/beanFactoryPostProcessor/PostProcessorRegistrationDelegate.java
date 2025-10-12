package org.example.spring.beanFactoryPostProcessor;

import org.example.spring.beanPostProcessor.BeanDefinitionRegistryPostProcessor;
import org.example.spring.beanPostProcessor.BeanFactoryPostProcessor;
import org.example.spring.context.beanFactory.BeanDefinitionRegistry;
import org.example.spring.context.beanFactory.BeanFactory;
import org.example.spring.context.beanFactory.ConfigurableListableBeanFactory;
import org.example.spring.context.beanFactory.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostProcessorRegistrationDelegate {
    private PostProcessorRegistrationDelegate(){}
    public static void registerBeanPostProcessors(DefaultListableBeanFactory beanFactory){
        // 思路：
        // 1.从beanFactory中获取已经注册的beanFactoryPostProcessor √
        // 2.把BeanDefinitionRegistryPostProcessor提取出来，然后进行调用扫描 √
        // 3.再次从beanFactory中获取beanFactoryPostProcessor
        // 4.根据order相关接口进行分类并且调用
        // 5.注意此前的调用皆为postProcessBeanDefinitionRegistry方法
        // 6.再次从beanFactory中获取beanFactoryPostProcessor
        // 7.根据order相关接口进行分类并且调用（postProcessBeanFactory方法）
        // 本人感觉application的初始化流程就是在此处完成的

        List<String> processingBeans = new ArrayList<>();//记录被扫描的Bean

        /// --------------------------一阶段--------------------------------///

        List<BeanFactoryPostProcessor> firstBeanFactoryPostProcessors = beanFactory.getBeanFactoryPostProcessors();
        List<BeanDefinitionRegistryPostProcessor> registryedPostProcessors = new ArrayList<>(); //这个相当于运行完方法的bean
        List<BeanFactoryPostProcessor> commonlyPostProcessors = new ArrayList<>();

        for(BeanFactoryPostProcessor beanFactoryPostProcessor : firstBeanFactoryPostProcessors){
            if(beanFactoryPostProcessor instanceof BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor){
                beanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(beanFactory);
                registryedPostProcessors.add(beanDefinitionRegistryPostProcessor);
            }
            else{
                commonlyPostProcessors.add(beanFactoryPostProcessor);
            }
        }

        /// --------------------------二阶段--------------------------------///

        List<BeanDefinitionRegistryPostProcessor> currentPostProcessors = new ArrayList<>(); //过程性的记录被扫描到的bean
        List<String> beanNameList = beanFactory.getBeanNameForType(BeanDefinitionRegistryPostProcessor.class);

        for(String beanName : beanNameList){
            if(beanFactory.isTypeMatch(beanName, PriorityOrdered.class)){
                BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = beanFactory.getBean(beanName, BeanDefinitionRegistryPostProcessor.class);
                currentPostProcessors.add(beanDefinitionRegistryPostProcessor);
                processingBeans.add(beanName);
            }
        }
        invokePostProcessors(currentPostProcessors, beanFactory, registryedPostProcessors );
        registryedPostProcessors.addAll(currentPostProcessors);
        sortPostProcessors(registryedPostProcessors);
        currentPostProcessors.clear();

        for(String beanName : beanNameList){
            if(!processingBeans.contains(beanName) && beanFactory.isTypeMatch(beanName, Ordered.class)){
                BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = beanFactory.getBean(beanName, BeanDefinitionRegistryPostProcessor.class);
                currentPostProcessors.add(beanDefinitionRegistryPostProcessor);
            }
        }
        invokePostProcessors(currentPostProcessors, beanFactory, registryedPostProcessors );
        registryedPostProcessors.addAll(currentPostProcessors);
        sortPostProcessors(registryedPostProcessors);
        currentPostProcessors.clear();

        Boolean isContinue = true;
        while(isContinue) {
            isContinue = false;
            beanNameList = beanFactory.getBeanNameForType(BeanDefinitionRegistryPostProcessor.class);
            for (String beanName : beanNameList) {
                if (!processingBeans.contains(beanName)) {
                    BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = beanFactory.getBean(beanName, BeanDefinitionRegistryPostProcessor.class);
                    currentPostProcessors.add(beanDefinitionRegistryPostProcessor);
                    processingBeans.add(beanName);
                    isContinue = true;
                }
            }
            invokePostProcessors(currentPostProcessors, beanFactory, registryedPostProcessors );
            registryedPostProcessors.addAll(currentPostProcessors);
            sortPostProcessors(registryedPostProcessors);
            currentPostProcessors.clear();
        }

        invokeBeanFactoryPostProcessors(registryedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(commonlyPostProcessors, beanFactory);

        /// ---------------------------三阶段--------------------------------///

        beanNameList = beanFactory.getBeanNameForType(BeanFactoryPostProcessor.class);
        List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
        List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
        List<BeanFactoryPostProcessor> commonPostProcessors = new ArrayList<>();


        for(String beanName : beanNameList){
            if(processingBeans.contains(beanName)){
               continue;
            }
            else if(beanFactory.isTypeMatch(beanName, PriorityOrdered.class)){
                BeanFactoryPostProcessor priorityOrdered = beanFactory.getBean(beanName, BeanFactoryPostProcessor.class);
                priorityOrderedPostProcessors.add(priorityOrdered);
            }
            else if(beanFactory.isTypeMatch(beanName, Ordered.class)){
                BeanFactoryPostProcessor ordered = beanFactory.getBean(beanName, BeanFactoryPostProcessor.class);
                orderedPostProcessors.add(ordered);
            }
            else{
                BeanFactoryPostProcessor beanFactoryPostProcessor = beanFactory.getBean(beanName, BeanFactoryPostProcessor.class);
                commonPostProcessors.add(beanFactoryPostProcessor);
            }
        }
       sortPostProcessors(priorityOrderedPostProcessors);
        sortPostProcessors(orderedPostProcessors);
        invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(commonPostProcessors, beanFactory);
    }

    private static void sortPostProcessors(List<? extends BeanFactoryPostProcessor> postProcessors){
        for(int i = 0; i < postProcessors.size(); i++){
            int max_index = i;
            for(int j = i; j < postProcessors.size(); j++){
                if (postProcessors.get(max_index) instanceof Ordered  ppm && postProcessors.get(j) instanceof Ordered ppj) {
                    if(ppj instanceof PriorityOrdered){
                        if(ppm instanceof PriorityOrdered && ppj.getOrder() > ppm.getOrder()){
                            max_index = j;
                        }
                        else if(!(ppm instanceof PriorityOrdered)){
                            max_index = j;
                        }
                    }
                    else if(!(ppm instanceof PriorityOrdered) && ppj.getOrder() > ppm.getOrder()){
                        max_index = j;
                    }
                }
            }
            Collections.swap(postProcessors, i, max_index);
        }
    }


    private static void invokePostProcessors(List<BeanDefinitionRegistryPostProcessor> postProcessors , DefaultListableBeanFactory beanFactory , List<BeanDefinitionRegistryPostProcessor> registeredPostProcessors){
        for(BeanDefinitionRegistryPostProcessor postProcessor : postProcessors){
            if(!registeredPostProcessors.contains(postProcessor)) {
                postProcessor.postProcessBeanDefinitionRegistry(beanFactory);
            }
        }
    }

    //这里的参数列表我是看spring源码的，虽然抄了但是我感觉确实牛逼
    private static void invokeBeanFactoryPostProcessors(List<? extends BeanFactoryPostProcessor> beanFactoryPostProcessors, DefaultListableBeanFactory beanFactory){
        for(BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors){
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }
}
