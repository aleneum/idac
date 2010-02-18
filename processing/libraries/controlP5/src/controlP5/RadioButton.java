package controlP5;

/**
 * controlP5 is a processing and java library for creating simple control GUIs.
 *
 *  2007 by Andreas Schlegel
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 * @author Andreas Schlegel (http://www.sojamo.de)
 *
 */

import java.util.Vector;

/**
 * a radio button implementation.
 * 
 * @related ControlP5
 * @related CheckBox
 * @related Toggle
 * 
 * @example ControlP5RadioButton
 * @nosuperclasses Controller
 * @related Controller
 */
public class RadioButton extends ControlGroup {

	protected Vector _myRadioToggles;

	protected int rowSpacing = 2;

	protected int columnSpacing = 2;

	protected int itemsPerRow = -1;

	protected boolean isMultipleChoice;

	protected int itemHeight = 11;

	protected int itemWidth = 11;

	/**
	 * a radioButton is a list of toggles that can be turned on or off.
	 * radioButton is of type ControllerGroup, therefore a controllerPlug can't
	 * be set. this means that an event from a radioButton can't be forwarded to
	 * a method other than controlEvent in a sketch.
	 * 
	 * a radioButton has 2 sets of values. radioButton.value() returns the value
	 * of the active radioButton item. radioButton.arrayValue() returns a float
	 * array that represents the active (1) and inactive (0) items of a
	 * radioButton.
	 * 
	 * @param theControlP5
	 * @param theParent
	 * @param theName
	 * @param theX
	 * @param theY
	 */
	public RadioButton(
		final ControlP5 theControlP5,
		final ControllerGroup theParent,
		final String theName,
		final int theX,
		final int theY) {
		super(theControlP5, theParent, theName, theX, theY, 100, 9);
		isBarVisible = false;
		isBarToggle = false;
		_myRadioToggles = new Vector();
		setItemsPerRow(1);
	}

	/**
	 * add items to a radioButton/checkBox. items are of type Toggle.
	 * 
	 * @param theName
	 * @param theValue
	 * @return
	 */
	public Toggle addItem(final String theName, final float theValue) {
		Toggle t = controlP5.addToggle(theName, 0, 0, itemWidth, itemHeight);
		t.captionLabel().style().marginLeft = t.width + 4;
		t.captionLabel().style().marginTop = -t.height - 2;
		return addItem(t, theValue);
	}

	/**
	 * add items to a radioButton/checkBox. items are of type Toggle.
	 * 
	 * @param theToggle
	 * @param theValue
	 * @return
	 */
	public Toggle addItem(final Toggle theToggle, final float theValue) {
		theToggle.setGroup(this);
		theToggle.isMoveable = false;
		theToggle.setInternalValue(theValue);
		theToggle.isBroadcast = false;
		_myRadioToggles.add(theToggle);
		updateLayout();
		color().copyTo(theToggle);
		theToggle.addListener(this);
		updateValues(false);
		return theToggle;
	}

	/**
	 * remove an item from the radioButton/checkBox list. The item is identified
	 * by name.
	 * 
	 * @param theName
	 */
	public void removeItem(final String theName) {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			if (((Toggle) _myRadioToggles.get(i)).name().equals(theName)) {
				((Toggle) _myRadioToggles.get(i)).removeListener(this);
				_myRadioToggles.remove(i);
			}
		}
		updateValues(false);
	}

	/**
	 * set the height of a radioButton/checkBox item. by default the height is
	 * 11px. in order to recognize a custom height, the itemHeight has to be set
	 * before adding items to a radioButton/checkBox.
	 * 
	 * @param theItemHeight
	 */
	public void setItemHeight(int theItemHeight) {
		itemHeight = theItemHeight;
	}

	/**
	 * set the width of a radioButton/checkBox item. by default the width is
	 * 11px. in order to recognize a custom width, the itemWidth has to be set
	 * before adding items to a radioButton/checkBox.
	 * 
	 * @param theItemWidth
	 */
	public void setItemWidth(int theItemWidth) {
		itemWidth = theItemWidth;
	}

	/**
	 * get an item from the list of toggles by index.
	 * 
	 * @param theIndex
	 * @return
	 */
	public Toggle getItem(int theIndex) {
		return ((Toggle) _myRadioToggles.get(theIndex));
	}

	/**
	 * get the state of a checkBox item - which can be true (for on) and false
	 * (for off). Identifier is the name of the checkBox item.
	 * 
	 * @param theIndex
	 * @return
	 */
	public boolean getState(int theIndex) {
		if (theIndex < _myRadioToggles.size() && theIndex >= 0) {
			return ((Toggle) _myRadioToggles.get(theIndex)).getState();
		}
		return false;
	}

	/**
	 * get the state of a checkBox item - which can be true (for on) and false
	 * (for off). Identifier is the name of the checkBox item.
	 * 
	 * @param theRadioButtonName
	 * @return
	 */
	public boolean getState(String theRadioButtonName) {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			if (theRadioButtonName.equals(t.name())) {
				return t.getState();
			}
		}
		return false;
	}

	public void updateLayout() {
		int nn = 0;
		int xx = 0;
		int yy = 0;
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			t.position().y = yy;
			t.position().x = xx;

			xx += t.width + columnSpacing;
			nn++;
			if (nn == itemsPerRow) {
				nn = 0;
				_myWidth = xx;
				yy += t.height + rowSpacing;
				xx = 0;
			}
			else {
				_myWidth = xx;
			}
		}
	}

	/**
	 * items for a radioButton or a checkBox are organized in columns and rows.
	 * setItemsPerRow sets the limit of items per row. items exceeding the limit
	 * will be pushed to the nxt row.
	 * 
	 * @param theValue
	 */
	public void setItemsPerRow(final int theValue) {
		itemsPerRow = theValue;
		updateLayout();
	}

	/**
	 * set the (pixel) spacing between columns.
	 * 
	 * @param theSpacing
	 */
	public void setSpacingColumn(final int theSpacing) {
		columnSpacing = theSpacing;
		updateLayout();
	}

	/**
	 * set the (pixel) spacing between rows.
	 * 
	 * @param theSpacing
	 */
	public void setSpacingRow(final int theSpacing) {
		rowSpacing = theSpacing;
		updateLayout();
	}

	/**
	 * deactivate all radioButton items.
	 * 
	 */
	public void deactivateAll() {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			((Toggle) _myRadioToggles.get(i)).deactivate();
		}
		_myValue = -1;
		updateValues(true);
	}

	/**
	 * deactivate the currently active radio button and make a new radio button
	 * active.
	 * 
	 * @param theIndex
	 */
	public void activate(int theIndex) {
		int n = _myRadioToggles.size();
		if (theIndex < n) {
			for (int i = 0; i < n; i++) {
				((Toggle) _myRadioToggles.get(i)).deactivate();
			}
			((Toggle) _myRadioToggles.get(theIndex)).activate();
			_myValue = ((Toggle) _myRadioToggles.get(theIndex)).value();
			updateValues(true);
		}
	}

	/**
	 * deactivate a radio button. the radio button must be active before
	 * deactivating to trigger an event.
	 * 
	 * @param theIndex
	 */
	public void deactivate(int theIndex) {
		if (theIndex < _myRadioToggles.size()) {
			Toggle t = ((Toggle) _myRadioToggles.get(theIndex));
			if (t.isActive) {
				t.deactivate();
				_myValue = -1;
				updateValues(true);
			}
		}
	}

	public void activate(String theRadioButtonName) {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			if (theRadioButtonName.equals(t.name())) {
				t.activate();
				_myValue = t.value();
				updateValues(true);
				return;
			}
		}
	}

	/**
	 * deactivate a RadioButton and set the value of the radio controller to
	 * the default value (-1).
	 * 
	 * @param theRadioButtonName
	 */
	public void deactivate(String theRadioButtonName) {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			if (theRadioButtonName.equals(t.name())) {
				t.deactivate();
				_myValue = -1;
				updateValues(true);
				return;
			}
		}
	}

	public void toggle(int theIndex) {
		// TODO
		// boolean itemState = ((Toggle)
		// _myRadioToggles.get(theIndex)).getState();
		// if (theIndex < _myRadioToggles.size()) {
		// Toggle t = ((Toggle) _myRadioToggles.get(theIndex));
		// if (t.isActive) {
		// t.deactivate();
		// _myValue = -1;
		// updateValues(true);
		// }
		// }
		System.out.println("RadioButton.toggle() not yet implemented, working on it.");
	}

	/**
	 * controlEvent is called whenever a radioButton item is (de-)activated.
	 * this happens internally.
	 * 
	 */
	public void controlEvent(ControlEvent theEvent) {
		if (!isMultipleChoice) {
			_myValue = -1;
			int n = _myRadioToggles.size();
			for (int i = 0; i < n; i++) {
				Toggle t = ((Toggle) _myRadioToggles.get(i));
				if (!t.equals(theEvent.controller())) {
					t.deactivate();
				}
				else {
					if (t.isOn) {
						_myValue = t.internalValue();
					}
				}
			}
		}
		updateValues(true);
	}

	protected void updateValues(boolean theBroadcastFlag) {
		int n = _myRadioToggles.size();
		_myArrayValue = new float[n];
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			_myArrayValue[i] = t.value();
		}
		if (theBroadcastFlag) {
			ControlEvent myEvent = new ControlEvent(this);
			controlP5.controlbroadcaster().broadcast(myEvent, ControlP5Constants.FLOAT);
		}
	}

}
