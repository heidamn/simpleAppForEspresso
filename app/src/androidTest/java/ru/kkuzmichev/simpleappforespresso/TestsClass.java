package ru.kkuzmichev.simpleappforespresso;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestsClass {
@Rule
public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void positiveCase() {
        ViewInteraction mainText = onView(withId(R.id.text_home));

        mainText.check(
                matches(
                        withText("This is home fragment")
                )
        );

    }
    @Test
    public void negativeCase() {
        ViewInteraction mainText = onView(withId(R.id.text_home));

        mainText.check(
                matches(
                        withText("This is NOT home fragment")
                )
        );

    }
}
