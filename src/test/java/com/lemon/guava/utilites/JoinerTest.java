package com.lemon.guava.utilites;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableBiMap.of;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;

/**
 * Created by lemoon on 2024/9/29 09:02
 */
public class JoinerTest {

    private final List<String> stringList = Arrays.asList(
            "Google", "Guava", "Java", "Scala", "Kafka"
    );

    private final List<String> stringListWithNullValue = Arrays.asList(
            "Google", "Guava", "Java", "Scala", null
    );

    private final Map<String, String> stringMap = of("Google", "Guava", "Java", "Scala");

    @Test
    public void testJoinOnJoin() {
        String result = Joiner.on("#").join(stringList);
        assertThat(result, equalTo("Google#Guava#Java#Scala#Kafka"));
    }

    @Test
    public void testJoinOnJoinWithNullValueButSkip() {
        String result = Joiner.on("#").skipNulls().join(stringListWithNullValue);
        assertThat(result, equalTo("Google#Guava#Java#Scala"));
    }

    @Test
    public void testJoin_On_Join_WithNullValue_UseDefaultValue() {
        String result = Joiner.on("#").useForNull("DEFAULT").join(stringListWithNullValue);
        assertThat(result, equalTo("Google#Guava#Java#Scala#DEFAULT"));
    }

    @Test
    public void testJoin_On_Append_To_StringBuilder() {
        StringBuilder builder = new StringBuilder();
        StringBuilder resultBuilder = Joiner.on("#").useForNull("DEFAULT").appendTo(builder, stringListWithNullValue);
        assertThat(resultBuilder, sameInstance(builder));
        assertThat(resultBuilder.toString(), equalTo("Google#Guava#Java#Scala#DEFAULT"));
    }

    private final String tragetFileName = "guava-joiner.text";
    private final String tragetFileNameToMap = "guava-joiner.text";

    @Test
    public void testJoin_On_Append_To_Writer() {
        try {
            FileWriter writer = new FileWriter(new File(tragetFileName));
            Joiner.on("#").useForNull("DEFAULT").appendTo(writer, stringListWithNullValue);
            assertThat(Files.isFile().test(new File(tragetFileName)), equalTo(true));
        } catch (IOException e) {
            fail("append to the writer occur fetal error.");
        }
    }

    @Test
    public void testJoiningByStream() {
        String result = stringListWithNullValue.stream().filter(item -> item != null && !item.isEmpty()).collect(joining("#"));
        System.out.println(result);
        assertThat(result, equalTo("Google#Guava#Java#Scala"));

    }

    @Test
    public void testJoiningByStreamWithDefaultValue() {
        String result = stringListWithNullValue.stream()
//                .map(item -> item == null || item.isEmpty() ? "DEFAULT" : item).collect(joining("#"));
                .map(this::defaultValue).collect(joining("#"));
        System.out.println(result);
        assertThat(result, equalTo("Google#Guava#Java#Scala#DEFAULT"));
    }

    private String defaultValue(String item) {
        return item == null || item.isEmpty() ? "DEFAULT" : item;
    }

    @Test
    public void testJoinOnWithMap() {
        String result = Joiner.on("#").withKeyValueSeparator("=").join(stringMap);
        System.out.println(result);
        assertThat(result, equalTo("Google=Guava#Java=Scala"));
    }

    @Test
    public void testJoinOnWithMapToAppendable() {
        try {
            FileWriter writer = new FileWriter(new File(tragetFileNameToMap));
            Joiner.on("#").withKeyValueSeparator("=").appendTo(writer, stringMap);
            assertThat(Files.isFile().test(new File(tragetFileNameToMap)), equalTo(true));
        } catch (IOException e) {
            fail("append to the writer occur fetal error.");
        }
    }
}
