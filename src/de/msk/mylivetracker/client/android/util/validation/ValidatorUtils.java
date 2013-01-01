package de.msk.mylivetracker.client.android.util.validation;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParams;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefs.LocalizationMode;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
 
/**
 * classname: ValidatorUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ValidatorUtils {

	public static boolean validateHttpParamName(
		Context ctx,
		HttpProtocolParams httpProtocolParams, 
		int position,
		EditText etHttpParamName) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx must not be null.");
		}
		if (httpProtocolParams == null) {
			throw new IllegalArgumentException("httpProtocolParams must not be null.");
		}
		if (etHttpParamName == null) {
			throw new IllegalArgumentException("etHttpParamName must not be null.");
		}
		boolean valid = true;
		String paramName = etHttpParamName.getText().toString();
		if (StringUtils.isEmpty(paramName)) {
			valid = false;
			SimpleInfoDialog.show(ctx, 
				ctx.getString(
					R.string.validator_valueMustNotBeEmpty, 
					ctx.getString(R.string.lbHttpProtocolParamsPrefs_ParameterName)));
		}
		if (!StringUtils.isAlpha(paramName)) {
			valid = false;
			SimpleInfoDialog.show(ctx, 
				ctx.getString(
					R.string.validator_valueMayHaveOnlyAlphaChars, 
					ctx.getString(R.string.lbHttpProtocolParamsPrefs_ParameterName)));
		}
		if (httpProtocolParams.paramNameExistsOutsidePosition(paramName, position)) {
			valid = false;
			SimpleInfoDialog.show(ctx, 
				ctx.getString(
					R.string.validator_valueAlreadyExists, 
					ctx.getString(R.string.lbHttpProtocolParamsPrefs_ParameterName)));
		}
		return valid;
	}
	
	public static boolean validateIfLocalizationModeIsSupported(
		Context ctx, LocalizationMode localizationMode, String localizationModeDisplayName) {
		if (!localizationMode.supported()) {
			String message = 
				ctx.getString(R.string.validator_locationProviderNotSupported, 
				localizationModeDisplayName);
			SimpleInfoDialog.show(ctx, message);
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
			SimpleInfoDialog.show(ctx, message);
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
			SimpleInfoDialog.show(ctx, message);
			return false;
		}
		if ((valueStr.length() < minLength) || 
			((maxLength >= minLength) && (valueStr.length() > maxLength))) {
			String message = 
				ctx.getString(R.string.validator_valueMustBeInRange, 
				ctx.getString(label), minLength, 
				(maxLength >= minLength ? maxLength : "..."));
			SimpleInfoDialog.show(ctx, message);
			return false;
		}
		return true;
	}
	
	public static boolean validateEditTextNumber(
		Context ctx, int label, EditText editText,
		boolean required, int minValue, int maxValue, boolean setFocusIfInvalid) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx must not be null.");
		}
		if (editText == null) {
			throw new IllegalArgumentException("editText must not be null.");
		}
		boolean valid = validateEditTextNumber(
			ctx, label, editText, required, minValue, maxValue);
				
		if (!valid && setFocusIfInvalid) {
			editText.requestFocus();
		}
		return valid;
	}
	
	private static boolean validateEditTextNumber(
		Context ctx, int label, EditText editText,
		boolean required,  
		int minValue, int maxValue) {
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
			SimpleInfoDialog.show(ctx, message);
			return false;
		}
		long value = -1;
		try {
			value = Long.parseLong(valueStr);
		} catch (NumberFormatException e) {
			String message =
				ctx.getString(R.string.validator_valueMustBeANumber,
				ctx.getString(label));
			SimpleInfoDialog.show(ctx, message);
			return false;
		}
		if ((value < minValue) || (value > maxValue)) {
			String message = 
				ctx.getString(R.string.validator_valueMustBeInRange, 
				ctx.getString(label), minValue, maxValue);
			SimpleInfoDialog.show(ctx, message);
			return false;
		}
		return true;
	}
	
}
