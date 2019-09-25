import {localizeText, localizeTextWithParams} from "../translator/Translator";

export const requiredField = value => {
    let requiredFieldErrorMsg = localizeText('field_required');
    if (value) return undefined;
    return requiredFieldErrorMsg;
};

export const maxLengthCreator = (maxValue) => value => {
    let maxValueErrorMsg = localizeTextWithParams("max_length {value}", {value: maxValue});
    if (value && value.length > maxValue) {
        return maxValueErrorMsg;
    }
    return undefined;
};
export const minLengthCreator = (minValue) => value => {
    let minValueErrorMsg = localizeTextWithParams("min_length {value}", {value: minValue});
    if (value && value.length < minValue) {
        return minValueErrorMsg;
    }
    return undefined;
};

export const passwordsMustMatch = (value, allValues) => {
    return value !== allValues.password ?
        localizeText('pass_doesnt_match') :
        undefined;
};