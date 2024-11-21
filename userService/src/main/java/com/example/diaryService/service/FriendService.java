package com.example.diaryService.service;

import com.example.diaryService.dto.response.FriendListResponseDto;
import com.example.diaryService.dto.response.UserSearchResponseDto;

import java.util.List;

public interface FriendService {

    void sendFriendRequest(Long requesterId, Long receiverId);

    List<UserSearchResponseDto> getFriendRequests(Long userId);

    void acceptFriendRequest(Long requesterId, Long receiverId);

    void rejectFriendRequest(Long friendRequestId);

    List<FriendListResponseDto> getFriendsList(Long userId);

    List<UserSearchResponseDto> searchUsersByUserName(Long userId, String userName);
}