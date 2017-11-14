package ru.spbau.group202.notdeadbydeadline.Controller;

import java.lang.ref.WeakReference;

import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;
import ru.spbau.group202.notdeadbydeadline.UI.AddHomeworkActivity;

public class Controller {

    // TODO most definitely should be a singleton
    // then there will be no point in statics?
    private static Homeworks homeworks = new Homeworks();

    public static AddHomeworkManager addHomeworkManager = new AddHomeworkManager(homeworks);
}
