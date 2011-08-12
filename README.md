Selenium Visual Diff
====================

Goal
----
The goal is a better integration of screenshots taking in maven executed selenium2
functional tests, storing and versioning of screenshots to get a report of visual differences
between two application versions.

Usage
-----
Embed jUnit4 and the selenium-visualdiff core library as dependencies in your funtional test maven module:

    <dependencies>
        ...
        <dependency>
            <groupId>org.kreyssel.selenium.visualdiff</groupId>
            <artifactId>visualdiff-core</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>
        ...
    </dependencies>

Add the selenium-visualdiff-maven-plugin to the maven module:

    <plugins>
        ...
        <plugin>
            <groupId>org.kreyssel.selenium.visualdiff</groupId>
            <artifactId>visualdiff-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare</goal>
                        <goal>package</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>