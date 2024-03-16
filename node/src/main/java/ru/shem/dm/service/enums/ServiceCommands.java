package ru.shem.dm.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommands(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }

    public static ServiceCommands fromValue(String cmd){
        for(ServiceCommands c : ServiceCommands.values()){
            if(c.value.equals(cmd)){
                return c;
            }
        }
        return null;
    }


}
