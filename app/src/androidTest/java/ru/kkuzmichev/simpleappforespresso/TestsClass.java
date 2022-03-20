package ru.kkuzmichev.simpleappforespresso;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.view.Gravity;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestsClass {

    @Before //Выполняется перед тестами
    public void registerIdlingResources() { //Подключаемся к “счетчику”
        IdlingRegistry.getInstance().register(EspressoIdlingResources.idlingResource);
    }

    @After // Выполняется после тестов
    public void unregisterIdlingResources() { //Отключаемся от “счетчика”
        IdlingRegistry.getInstance().unregister(EspressoIdlingResources.idlingResource);
    }

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void positiveTest() {
        ViewInteraction mainText = onView(withId(R.id.text_home));

        mainText.check(
                matches(
                        withText("This is home fragment")
                )
        );

    }

    @Test
    public void negativeTest() {
        ViewInteraction mainText = onView(withId(R.id.text_home));

        mainText.check(
                matches(
                        withText("This is NOT home fragment")
                )
        );

    }

    @Test
    public void galleryTest() throws InterruptedException {
        ViewInteraction menuButton = onView(withId(R.id.nav_gallery));
        ViewInteraction drawer = onView(withId(R.id.drawer_layout));
        drawer.check(matches(isClosed(Gravity.START))).perform(DrawerActions.open());
        menuButton.perform(click());
        ViewInteraction recyclerView = onView(withId(R.id.recycle_view));
        recyclerView.check(CustomViewAssertions.isRecyclerView());
        recyclerView.check(matches(CustomViewMatcher.recyclerViewSizeMatcher(10)));
        Thread.sleep(2000);
        ViewInteraction itemInRecyclerView = onView(allOf(withId(R.id.item_number), withText("1")));
        itemInRecyclerView.check(matches(isDisplayed()));
    }

    @Test
    public void intentTest() {
        Intents.init();
        ViewInteraction options = onView(withContentDescription("More options"));
        options.perform(click());
        ViewInteraction settings = onView(withText("Settings"));
        settings.perform(click());
        intended(hasData("https://google.com"));
        intended(hasAction(Intent.ACTION_VIEW));
        Intents.release();
    }


}
