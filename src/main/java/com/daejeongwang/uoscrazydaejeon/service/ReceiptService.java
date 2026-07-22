package com.daejeongwang.uoscrazydaejeon.service;

import com.daejeongwang.uoscrazydaejeon.dto.response.MemberResponse;
import com.daejeongwang.uoscrazydaejeon.repository.ReceiptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final MemberService memberService;

    public MemberResponse getTmpMember() {
        return memberService.findMemberById(1L);
    }

}
