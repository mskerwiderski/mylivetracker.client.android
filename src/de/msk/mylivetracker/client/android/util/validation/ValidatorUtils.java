package de.msk.mylivetracker.client.android.util.validation;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.preferences.Preferences.LocalizationMode;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
 
/**
 * ValidatorUtils.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 001 2012-02-04 localizationModes implemented (gps, network, gpsAndNetwork).
 * 000 2011-08-11 initial.
 * 
 */
public class ValidatorUtils {

	public static boolean validatePinCode(
		Context ctx, EditText etPinCode) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx must not be null.");
		}
		if (etPinCode == null) {
			throw new IllegalArgumentException("etPinCode must not be null.");
		}
		boolean valid = StringUtils.equals(
			etPinCode.getText().toString(), 
			Preferences.get().getPinCode());
		if (!valid) {
			new SimpleInfoDialog(ctx, R.string.validator_pinCodeInvalid).show();
		}
		return valid;
	}
	
	public static boolean validatePinCodeEqualsPinCodeReentered(
		Context ctx, EditText etPinCode, EditText etPinCodeReentered) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx must not be null.");
		}
		if (etPinCode == null) {
			throw new IllegalArgumentException("etPinCode must not be null.");
		}
		if (etPinCodeReentered == null) {
			throw new IllegalArgumentException("etPinCodeReentered must not be null.");
		}
		boolean valid = StringUtils.equals(
			etPinCode.getText().toString(), 
			etPinCodeReentered.getText().toString());
		if (!valid) {
			new SimpleInfoDialog(ctx, R.string.validator_reenteredPinCodeNotEqualToPinCode).show();
		}
		return valid;
	}
	
	public static boolean validateIfLocalizationModeIsSupported(
		Context ctx, LocalizationMode localizationMode, String localizationModeDisplayName) {
		if (!localizationMode.supported()) {
			String message = 
				ctx.getString(R.string.validator_locationProviderNotSupported, 
				localizationModeDisplayName);
			new SimpleInfoDialog(ctx, message).show();
			return false;
		}
		return true;
	}
	
	public static boolean validateIfPhoneNumber(
		Context ctx, int label, EditText editText,
		boolean setFocusIfInvalid) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx must not be null.");
		}
		if (editText == null) {
			throw new IllegalArgumentException("editText must not be null.");
		}
		boolean valid = PhoneNumberUtils.isGlobalPhoneNumber(editText.getText().toString());
		if (!valid) {
			String message = 
				ctx.getString(R.string.validator_phoneNumberInvalid, 
				ctx.getString(label));
			new SimpleInfoDialog(ctx, message).show();
		}
		if (!valid && setFocusIfInvalid) {
			editText.requestFocus();
		}
		return valid;
	}
	
	public static boolean validateEditTextString(
		Context ctx, int label, EditText editText,
		int minLength, int maxLength, boolean setFocusIfInvalid) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx must not be null.");
		}
		if (editText == null) {
			throw new IllegalArgumentException("editText must not be null.");
		}
		boolean valid = validateEditTextString(ctx, label, editText, minLength, maxLength);
		if (!valid && setFocusIfInvalid) {
			editText.requestFocus();
		}
		return valid;
	}
	
	private static boolean validateEditTextString(
		Context ctx, int label, EditText editText,
		int minLength, int maxLength) {		
		if (minLength < 0) {
			minLength = 0;
		}
		String valueStr = editText.getText().toString();
		if ((minLength == 0) && StringUtils.isEmpty(valueStr)) {
			return true;
		}
		if ((minLength > 0) && StringUtils.isEmpty(valueStr)) {
			String message = 
				ctx.getString(R.string.validator_valueMustNotBeEmpty, 
				ctx.getString(label));
			new SimpleInfoDialog(ctx, message).show();
			return false;
		}
		if ((valueStr.length() < minLength) || 
			((maxLength >= minLength) && (valueStr.length() > maxLength))) {
			String message = 
				ctx.getString(R.string.validator_valueMustBeInRange, 
				ctx.getString(label), minLength, 
				(maxLength >= minLength ? maxLength : "..."));
			new SimpleInfoDialog(ctx, message).show();
			return false;
		}
		return true;
	}
	
	public static boolean validateEditTextNumber(
		Context ctx, int label, EditText editText,
		boolean required, int minDigit, int maxDigit, boolean setFocusIfInvalid) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx must not be null.");
		}
		if (editText == null) {
			throw new IllegalArgumentException("editText must not be null.");
		}
		boolean valid = validateEditTextNumber(ctx, label, editText, required, minDigit, maxDigit);
				
		if (!valid && setFocusIfInvalid) {
			editText.requestFocus();
		}
		return valid;
	}
	
	private static boolean validateEditTextNumber(
		Context ctx, int label, EditText editText,
		boolean required,  
		int minDigit, int maxDigit) {
		if (editText == null) {
			throw new IllegalArgumentException("editText must not be null.");
		}
		String valueStr = editText.getText().toString();
		if (!required && StringUtils.isEmpty(valueStr)) {
			return true;
		}
		if (required && StringUtils.isEmpty(valueStr)) {
			String message = 
				ctx.getString(R.string.validator_valueMustNotBeEmpty, 
				ctx.getString(label));
			new SimpleInfoDialog(ctx, message).show();
			return false;
		}
		long value = -1;
		try {
			value = Long.parseLong(valueStr);
		} catch (NumberFormatException e) {
			String message =
				ctx.getString(R.string.validator_valueMustBeANumber,
				ctx.getString(label));
			new SimpleInfoDialog(ctx, message).show();
			return false;
		}
		if ((value < minDigit) || (value > maxDigit)) {
			String message = 
				ctx.getString(R.string.validator_valueMustBeInRange, 
				ctx.getString(label), minDigit, maxDigit);
			new SimpleInfoDialog(ctx, message).show();
			return false;
		}
		return true;
	}
	
}
