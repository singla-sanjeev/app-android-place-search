package com.homeaway.placesearch;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.homeaway.placesearch.ui.MainActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class VenueSearchTest {

    private static final String SEARCH_QUERY = "Amazon";
    private IdlingResource mIdlingResource;
    private ActivityScenario mActivityScenario;


    @Before
    public void init() {
        mActivityScenario = ActivityScenario.launch(MainActivity.class);

        mActivityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                mIdlingResource = activity.getIdlingResource();
                // To prove that the test fails, omit this call:
                IdlingRegistry.getInstance().register(mIdlingResource);
            }
        });
    }

    @Test
    public void testSearchPlacesScreenWithMoreThanOnceChar() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        onView(withId(R.id.mapFab)).check(matches(isDisplayed()));
    }

    @Test
    public void testLaunchMapViewOfPlaces() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        onView(withId(R.id.mapFab)).check(matches(isDisplayed())).perform(click());

        pauseTestFor(10000);

        pressBack();
    }

    @Test
    public void testListItemCountAssertion() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(10)));
    }

    @Test
    public void testLaunchOfVenueDetailsOfFirstListItem() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        pauseTestFor(6000);

        pressBack();
    }

    @Test
    public void testListItemFavoriteIconClick() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(3, new ClickOnImageView()));

        pauseTestFor(2000);
    }

    @Test
    public void testVenueDetailsVenueName() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.txtVwName)).check(matches(ViewMatchers.isDisplayed()));

        pauseTestFor(2000);

        pressBack();
    }

    @Test
    public void testVenueDetailsAddress() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.txtVwAddress)).check(matches(ViewMatchers.isDisplayed()));

        pauseTestFor(2000);

        pressBack();
    }

    @Test
    public void testVenueDetailsCategoryIcon() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.imgViewCategoryIcon)).check(matches(ViewMatchers.isDisplayed()));

        pauseTestFor(2000);

        pressBack();
    }

    @Test
    public void testVenueDetailsCategory() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.txtVwCategory)).check(matches(ViewMatchers.isDisplayed()));

        pauseTestFor(2000);

        pressBack();
    }

    @Test
    public void testVenueDetailsDistance() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.txtVwDistance)).check(matches(ViewMatchers.isDisplayed()));

        pauseTestFor(2000);

        pressBack();
    }

    @Test
    public void testVenueDetailsUrl() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        pauseTestFor(2000);

        onView(withId(R.id.txtVwLink)).check(matches(ViewMatchers.isDisplayed()));

        pauseTestFor(2000);

        pressBack();
    }

    @Test
    public void testVenueDetailsUrlClickable() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        pauseTestFor(2000);

        onView(withId(R.id.txtVwLink)).check(matches(ViewMatchers.isClickable()));

        pauseTestFor(2000);

        pressBack();
    }

    @Test
    public void testVenueDetailsLaunchUrl() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        pauseTestFor(2000);

        onView(withId(R.id.txtVwLink)).check(matches(ViewMatchers.isClickable())).perform(click());

        pauseTestFor(2000);
    }

    @Test
    public void testVenueDetailsFavoriteClick() {
        onView(withId(R.id.searchPlaceEdtVw))
                .perform(typeText(SEARCH_QUERY), closeSoftKeyboard());

        pauseTestFor(500);

        onView(withId(R.id.venueListRecyclerVw)).check(new RecyclerViewItemCountAssertion(greaterThan(5))).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        pauseTestFor(2000);

        onView(withId(R.id.favoriteFab)).perform(click());
        pauseTestFor(200);
        onView(withId(R.id.favoriteFab)).perform(click());
        pauseTestFor(200);
        onView(withId(R.id.favoriteFab)).perform(click());

        pauseTestFor(500);

        pressBack();
    }


    private void pauseTestFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

    private static class RecyclerViewItemCountAssertion implements ViewAssertion {

        private final Matcher<Integer> matcher;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.matcher = is(expectedCount);
        }

        public RecyclerViewItemCountAssertion(Matcher<Integer> matcher) {
            this.matcher = matcher;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), matcher);
        }

    }

    private static class ClickOnImageView implements ViewAction {
        ViewAction click = click();

        @Override
        public Matcher<View> getConstraints() {
            return click.getConstraints();
        }

        @Override
        public String getDescription() {
            return "Click on favorite icon image view";
        }

        @Override
        public void perform(UiController uiController, View view) {
            click.perform(uiController, view.findViewById(R.id.favoriteIconImgVw));
        }
    }

}
