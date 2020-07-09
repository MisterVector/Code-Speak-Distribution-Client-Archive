package org.codespeak.distribution.client;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class containing the settings of the client
 *
 * @author Vector
 */
public class Settings {

    public enum SettingFields {
        REMEMBER_SELECTED_CATEGORY("remember_selected_category", Boolean.class, false),
        CHECK_CLIENT_UPDATE_ON_STARTUP("check_client_update_on_startup", Boolean.class, true),
        BACKUP_BEFORE_REMOVING_TEXT_FILES("backup_before_removing_text_files", Boolean.class, true);
        
        private final String key;
        private final Class fieldClass;
        private final Object defaultValue;
        
        private SettingFields(String key, Class fieldClass, Object defaultValue) {
            this.key = key;
            this.fieldClass = fieldClass;
            this.defaultValue = defaultValue;
        }
        
        /**
         * Gets the key of this field
         * @return key of this field
         */
        public String getKey() {
            return key;
        }
        
        /**
         * Gets the class representing this field
         * @return class representing this field
         */
        public Class getFieldClass() {
            return fieldClass;
        }
        
        /**
         * Gets the default value of this field
         * @return default value of this field
         */
        public Object getDefaultValue() {
            return defaultValue;
        }
    }
    
    private Map<SettingFields, Object> fieldValues = new HashMap<SettingFields, Object>();
    
    private Settings(Map<SettingFields, Object> fieldValues) {
        this.fieldValues = fieldValues;
    }
    
    /**
     * Gets the value of the specified Settings field
     * @param field field representing the value to return
     * @return value of the specified settings field
     */
    public <T> T getValue(SettingFields field) {
        return (T) fieldValues.get(field);
    }
    
    /**
     * Sets the value of the specified Settings field
     * @param field field representing the value to set
     * @param value value to set
     */
    public void setValue(SettingFields field, Object value) {
        fieldValues.put(field, value);
    }
    
    /**
     * Converts this Settings object to JSON
     * @return JSON representation of this Settings object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        for (EnumMap.Entry<SettingFields, Object> entry : fieldValues.entrySet()) {
            SettingFields field = entry.getKey();
            Object value = entry.getValue();
            
            json.put(field.getKey(), value);
        }
        
        return json;
    }
    
    /**
     * Creates a Settings object from JSON
     * @param json JSON representation to create a Settings object from
     * @return Settings object created from JSON
     */
    public static Settings fromJSON(JSONObject json) {
        Map<SettingFields, Object> fieldValues = new HashMap<SettingFields, Object>();
        
        for (SettingFields field : SettingFields.values()) {
            String key = field.getKey();
            Class fieldClass = field.getFieldClass();
            Object value = null;
            
            if (json.has(key)) {
                try {
                    Object tempValue = json.get(key);
                    
                    if (fieldClass == Boolean.class) {
                        if (tempValue instanceof Boolean) {
                            value = tempValue;
                        }
                    }
                } catch (JSONException ex) {
                    
                }
            }
            
            if (value == null) {
                value = field.getDefaultValue();
            }
            
            fieldValues.put(field, value);
        }
        
        return new Settings(fieldValues);
    }
    
}
