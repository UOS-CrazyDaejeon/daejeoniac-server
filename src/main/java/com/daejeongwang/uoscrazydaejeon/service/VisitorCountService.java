package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.api.CongestionItemResponse;
import com.daejeongwang.uoscrazydaejeon.entity.Congestion;
import com.daejeongwang.uoscrazydaejeon.repository.VisitorCountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class VisitorCountService {
    private VisitorCountRepository visitorCountRepository;

    @Transactional
    public void syncVisitorCount() {

    }
}
