package ru.spbau.group202.notdeadbydeadline.model;


import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/*
Этот интерфес - гиганский оверинжиниринг.
Насколько я видел - каждый раз, когда вызывается метод getDetails тип был известен.
Этот подход можно использовать, если у нас есть каша из разных наследников DetailedEntry
в одной структуре данных и мы хотим ее отобразить. Тут же - вполне хватит никак не связанных друг
с другом методов в наследниках вида - allFieldsToStringList (details - дурацкое название).
Лучше даже не в самом классе, а в каких то утилах или просто как метод по месту использования,
если оно всего одно.

Брр. Мне кажется я понял. Это все делается для getEntriesDetailList и getEntriesDeconstructedList?
Попытка написать свой map? Выставили бы 24 API и писали спокойно на стримах с лямбдами.
Или на котлине. Или уже по честному написали бы map вида list<T> -> list<T1> c функтором.
Или не делать

Резюме такое - выпилить этот интерфейс нафиг.
*/
public interface DetailedEntry {
    @NotNull
    ArrayList<String> getDetails();

/*
    У меня нет интернета и я не знаю как работает вся каша с Parcelable, тут явно что то не так.
    Тут должен быть какой то родной интерфейс вида Serialiable (Bundable)
    Я с ходу вообще не понял всей этой системы с deconstructed.
    Строки вида getEntriesDeconstructedList(homeworks).get(0); вооще не добавляют понимания.
    Почему именно 0? Что за захардкоженная константа?
     */
    @NotNull
    Bundle getDeconstructed();
}
