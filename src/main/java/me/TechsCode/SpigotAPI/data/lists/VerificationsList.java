package me.TechsCode.SpigotAPI.data.lists;

import me.TechsCode.SpigotAPI.data.UserVerification;
import me.TechsCode.SpigotAPI.data.UserVerificationStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class VerificationsList extends ArrayList<UserVerification> {

    public VerificationsList(int initialCapacity) {
        super(initialCapacity);
    }

    public VerificationsList() {}

    public VerificationsList(Collection<? extends UserVerification> c) {
        super(c);
    }

    public Optional<UserVerification> userId(String userId){
        return stream().filter(verification -> verification.getUserId().equals(userId)).collect(Collectors.toCollection(VerificationsList::new)).stream().findFirst();
    }

    public Optional<UserVerification> code(String code){
        return stream().filter(verification -> verification.getCode().equalsIgnoreCase(code)).collect(Collectors.toCollection(VerificationsList::new)).stream().findFirst();
    }

    public VerificationsList verificationChecked(boolean verified){
        return stream().filter(verification -> verification.isverificationChecked() == verified).collect(Collectors.toCollection(VerificationsList::new));
    }

    public VerificationsList verificationStatus(UserVerificationStatus verificationStatus){
        return stream().filter(verification -> verification.getVerificationStatus() == verificationStatus).collect(Collectors.toCollection(VerificationsList::new));
    }

    public VerificationsList minutesPast(int minutes){
        long now = System.currentTimeMillis() / 1000;
        return stream().filter(verification -> now > verification.getRequestedTime() + (60L * minutes) ).collect(Collectors.toCollection(VerificationsList::new));
    }

}
