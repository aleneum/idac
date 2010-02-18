package controlP5;

/**
 * A checkBox is a multiple-choice radioButton. items are added to a checkBox
 * and can be organized is rows and columns. items of a checkBox are of type
 * Toggle.
 * 
 * @author andreas schlegel
 * @example ControlP5CheckBox
 * @related Toggle
 * 
 */
public class CheckBox extends RadioButton {

	/**
	 * a CheckBox should only be added to controlP5 by using
	 * controlP5.addCheckBox()
	 * 
	 * @param theControlP5
	 * @param theParent
	 * @param theName
	 * @param theX
	 * @param theY
	 */
	public CheckBox(
		final ControlP5 theControlP5,
		final ControllerGroup theParent,
		final String theName,
		final int theX,
		final int theY) {
		super(theControlP5, theParent, theName, theX, theY);
		isMultipleChoice = true;
	}

	/**
	 * activate all checkBox items.
	 */
	public final void activateAll() {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			((Toggle) _myRadioToggles.get(i)).activate();
		}
		updateValues();
	}

	/**
	 * activate an individual item of the checkox-item-list.
	 */
	public final void activate(int theIndex) {
		if (theIndex < _myRadioToggles.size()) {
			((Toggle) _myRadioToggles.get(theIndex)).activate();
			updateValues();
		}
	}

	public final void deactivate(int theIndex) {
		if (theIndex < _myRadioToggles.size()) {
			((Toggle) _myRadioToggles.get(theIndex)).deactivate();
			updateValues();
		}
	}

	public final void toggle(int theIndex) {
		if (theIndex < _myRadioToggles.size()) {
			Toggle t = ((Toggle) _myRadioToggles.get(theIndex));
			if (t.getState() == true) {
				t.deactivate();
			}
			else {
				t.activate();
			}
			updateValues();
		}
	}

	public final void toggle(String theRadioButtonName) {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			if (theRadioButtonName.equals(t.name())) {

				if (t.getState() == true) {
					t.deactivate();
				}
				else {
					t.activate();
				}
				updateValues();
				return;
			}
		}
	}

	public final void activate(String theRadioButtonName) {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			if (theRadioButtonName.equals(t.name())) {
				t.activate();
				updateValues();
				return;
			}
		}
	}

	public final void deactivate(String theRadioButtonName) {
		int n = _myRadioToggles.size();
		for (int i = 0; i < n; i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			if (theRadioButtonName.equals(t.name())) {
				t.deactivate();
				updateValues();
				return;
			}
		}
	}

	private final void updateValues() {
		_myValue = -1;
		updateValues(true);
	}
}
