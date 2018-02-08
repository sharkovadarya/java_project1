package ru.spbau.group202.notdeadbydeadline.model;


import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface DetailedEntry {
    @NotNull
    ArrayList<String> getDetails();

    @NotNull
    Bundle getDeconstructed();
}
