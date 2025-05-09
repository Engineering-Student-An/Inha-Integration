package an.inhaintegration.rentalee.service;

import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnivInfoService {

    private final UnivInfoRepository univInfoRepository;

    @Transactional
    public void create(CustomUserDetails userDetails, String password) {


    }

//    private final
}
