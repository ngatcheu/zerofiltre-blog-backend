package tech.zerofiltre.blog.domain.course.use_cases.subscription;

import tech.zerofiltre.blog.domain.*;
import tech.zerofiltre.blog.domain.course.*;
import tech.zerofiltre.blog.domain.course.model.*;
import tech.zerofiltre.blog.domain.error.*;

public class CompleteLesson {

    private final SubscriptionProvider subscriptionProvider;
    private final LessonProvider lessonProvider;
    private final ChapterProvider chapterProvider;

    public CompleteLesson(SubscriptionProvider subscriptionProvider, LessonProvider lessonProvider, ChapterProvider chapterProvider) {
        this.subscriptionProvider = subscriptionProvider;
        this.lessonProvider = lessonProvider;
        this.chapterProvider = chapterProvider;
    }

    public Subscription execute(long courseId, long lessonId, long currentUserId) throws ResourceNotFoundException, ForbiddenActionException {
        Subscription existingSubscription = subscriptionProvider.subscriptionOf(currentUserId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no subscription regarding the courseId and userId you submit", "Course Id = " + courseId + " " + "UserId = " + currentUserId, Domains.COURSE.name()));

        Lesson lesson = lessonProvider.lessonOfId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson of id " + lessonId + " does not exist", String.valueOf(lessonId), Domains.COURSE.name()));

        ForbiddenActionException forbiddenActionException = new ForbiddenActionException("Lesson not part of this subscription", Domains.COURSE.name());

        Chapter chapter = chapterProvider.chapterOfId(lessonId)
                .orElseThrow(() -> forbiddenActionException);

        if (chapter.getCourseId() != courseId) throw forbiddenActionException;
        existingSubscription.getCompletedLessons().add(lesson);

        return subscriptionProvider.save(existingSubscription);
    }


}
