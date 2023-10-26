package ir.fum.oop.pattern.description;

import ir.fum.oop.model.ClassInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
@Setter
public class SystemDescription {
    private List<ClassInfo> classInfos;
    private Map<String, ClassInfo> classInfoMap;
    private Map<String, Integer> classIndexLocator;

    private int numberOfClasses;

    public SystemDescription(Map<String, ClassInfo> classInfoMap) {
        this.classInfos = new ArrayList<>(classInfoMap.values());
        this.numberOfClasses = classInfos.size();
        this.classInfoMap = classInfoMap;

        this.classIndexLocator = classInfos.stream()
                .collect(Collectors.toMap(ClassInfo::getClassName, classInfos::indexOf));



    }
}
