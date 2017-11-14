package ru.spbau.group202.notdeadbydeadline.Controller;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeworkDatabaseControllerTest {
    HomeworkDatabaseController bd;

    @Before
    public void setUp(){
        bd = new HomeworkDatabaseController(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void finish() {
        bd.close();
    }

    @Test
    public void addHomework() throws Exception {
        Homework hw = new Homework(2017, 11, 15, 14, 33,
                "f", false, "h", "r", 12);
        bd.addHomework(hw);
    }


    @Test
    public void getActualHomeworks() throws Exception {
    }

    @Test
    public void getHomeworksBySubject() throws Exception {
    }

    @Test
    public void getHomeworksByDay() throws Exception {


}