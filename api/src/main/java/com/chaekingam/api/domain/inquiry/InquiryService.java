package com.chaekingam.api.domain.inquiry;

import com.chaekingam.api.domain.inquiry.dto.InquiryCreateRequest;
import com.chaekingam.api.domain.inquiry.dto.InquiryResponse;
import com.chaekingam.api.domain.user.User;
import com.chaekingam.api.domain.user.UserRepository;
import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import com.chaekingam.api.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Transactional
    public InquiryResponse create(InquiryCreateRequest req, Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        if (user == null && (req.guestName() == null || req.guestEmail() == null)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Inquiry inquiry = Inquiry.create(req.title(), req.content(), user,
                req.guestName(), req.guestEmail());
        InquiryResponse saved = InquiryResponse.from(inquiryRepository.save(inquiry));
        String authorName = user != null ? user.getNickname() : req.guestName();
        mailService.sendInquiryNotification(authorName, req.title());
        return saved;
    }

    /** 내 문의 목록 — 회원이면 userId, 비회원이면 guestEmail로 조회 */
    public List<InquiryResponse> getMyInquiries(Long userId, String guestEmail) {
        if (userId != null) {
            return inquiryRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                    .stream().map(InquiryResponse::summary).toList();
        }
        if (guestEmail != null) {
            return inquiryRepository.findAllByGuestEmailAndDeletedAtIsNullOrderByCreatedAtDesc(guestEmail)
                    .stream().map(InquiryResponse::summary).toList();
        }
        return List.of();
    }

    public InquiryResponse getDetail(Long id, Long userId, String guestEmail) {
        Inquiry inquiry = inquiryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        if (!isAdminUser(userId)) {
            assertAccess(inquiry, userId, guestEmail);
        }
        return InquiryResponse.from(inquiry);
    }

    @Transactional
    public InquiryResponse update(Long id, InquiryCreateRequest req, Long userId, String guestEmail) {
        Inquiry inquiry = inquiryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        assertAccess(inquiry, userId, guestEmail);
        inquiry.update(req.title(), req.content());
        return InquiryResponse.from(inquiry);
    }

    @Transactional
    public void delete(Long id, Long userId, String guestEmail) {
        Inquiry inquiry = inquiryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        assertAccess(inquiry, userId, guestEmail);
        inquiry.softDelete();
    }

    private boolean isAdminUser(Long userId) {
        if (userId == null) return false;
        return userRepository.findById(userId).map(User::isAdmin).orElse(false);
    }

    private void assertAccess(Inquiry inquiry, Long userId, String guestEmail) {
        if (userId != null) {
            if (inquiry.getUser() == null || !inquiry.getUser().getId().equals(userId)) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        } else {
            if (guestEmail == null || !guestEmail.equals(inquiry.getGuestEmail())) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        }
    }
}
