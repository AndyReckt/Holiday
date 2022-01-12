package io.github.damt.menu.pagination;

import io.github.damt.menu.buttons.Button;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum NavigationPosition {

    BOTTOM {
        /**
         * Get the navigation buttons
         * for the position type.
         *
         * @return the buttons
         */
        @Override
        public Map<Integer, Button> getNavigationButtons(PaginatedMenu menu) {
            final Map<Integer, Button> map = new HashMap<>();

            map.put(menu.getSize() - 9, menu.getPreviousPageButton().setClickAction(event -> {
                menu.navigatePrevious();
                event.setCancelled(true);
            }));

            map.put(menu.getSize() - 1, menu.getNextPageButton().setClickAction(event -> {
                menu.navigateNext();
                event.setCancelled(true);
            }));

            return map;
        }

        /**
         * Get a list of buttons in the range of the
         * current menu's page.
         *
         * @param buttons the list of buttons to get the buttons in range from
         * @param menu    the menu to get the data from
         * @return the buttons in range
         */
        @Override
        public Map<Integer, Button> getButtonsInRange(Map<Integer, Button> buttons, PaginatedMenu menu) {
            final Map<Integer, Button> map = new HashMap<>();

            final int size = menu.getSize();
            final int page = menu.getPage();

            final int maxElements = size - 9;

            final int start = (page - 1) * maxElements;
            final int end = (start + maxElements) - 1;

            for (int index = 0; index < buttons.size(); index++) {
                final Button button = buttons.get(index);

                if (button != null && index >= start && index <= end) {
                    map.put(index - ((maxElements) * (page - 1)), button);
                }
            }

            final Map<Integer, Button> navigationBar = menu.getNavigationBar();

            for (int index = 0; index < navigationBar.size(); index++) {
                final Button button = navigationBar.get(index);

                if (button != null) {
                    map.put(index, button);
                }
            }

            return map;
        }
    };

    /**
     * Get the navigation buttons
     * for the position type.
     *
     * @param menu the menu to get the data from
     * @return the buttons
     */
    public abstract Map<Integer, Button> getNavigationButtons(PaginatedMenu menu);

    /**
     * Get a list of buttons in the range of the
     * current menu's page.
     *
     * @param buttons the list of buttons to get the buttons in range from
     * @param menu    the menu to get the data from
     * @return the buttons in range
     */
    public abstract Map<Integer, Button> getButtonsInRange(Map<Integer, Button> buttons, PaginatedMenu menu);

}