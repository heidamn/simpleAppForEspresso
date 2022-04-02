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
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.Matchers.allOf;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.Allure;

@RunWith(AllureAndroidJUnit4.class)
public class TestsClass {

    @Before //Выполняется перед тестами
    public void registerIdlingResources() { //Подключаемся к “счетчику”
        IdlingRegistry.getInstance().register(EspressoIdlingResources.idlingResource);
        Intent intent = new Intent(Intent.ACTION_PICK);
        //this is the key part
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //this is the key part
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @After // Выполняется после тестов
    public void unregisterIdlingResources() { //Отключаемся от “счетчика”
        IdlingRegistry.getInstance().unregister(EspressoIdlingResources.idlingResource);
    }

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );

    @Rule
    public TestWatcher watcher = new TestWatcher() {

        @Override
        protected void failed(Throwable e, Description description) {
            String className = description.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1);
            String methodName = description.getMethodName();
            takeScreenshot(className + "#" + methodName);
        }

        private void takeScreenshot(String name) {
            File path = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/screenshots/");

            if (!path.exists()) {
                path.mkdirs();
            }

            UiDevice device = UiDevice.getInstance(getInstrumentation());
            String filename = name + ".png";

            device.takeScreenshot(new File(path, filename));

            try {
                Allure.attachment(filename, new FileInputStream(new File(path, filename)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

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
    public void intentTest() throws InterruptedException {
        ViewInteraction options = onView(withContentDescription("More options"));
        options.perform(click());
        ViewInteraction settings = onView(withText("Settings"));
        Intents.init();
        settings.perform(click());
        intended(hasData("https://google.com"));
        intended(hasAction(Intent.ACTION_VIEW));
        Intents.release();
    }


}
