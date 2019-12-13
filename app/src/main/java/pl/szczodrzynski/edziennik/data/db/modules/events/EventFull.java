package pl.szczodrzynski.edziennik.data.db.modules.events;

import pl.szczodrzynski.edziennik.data.db.modules.metadata.Metadata;

public class EventFull extends Event {
    public String typeName = "";
    public int typeColor = -1;

    public String teacherFullName = "";

    public String subjectLongName = "";
    public String subjectShortName = "";

    public String teamName = "";
    public String teamCode = null;

    // metadata
    public boolean seen;
    public boolean notified;
    public long addedDate;

    public EventFull() {}

    public EventFull(Event event) {
        super(
                event.profileId,
                event.id,
                event.eventDate.clone(),
                event.startTime == null ? null : event.startTime.clone(),
                event.topic,
                event.color,
                event.type,
                event.addedManually,
                event.subjectId,
                event.teacherId,
                event.teamId
        );

        this.sharedBy = event.sharedBy;
        this.sharedByName = event.sharedByName;
        this.blacklisted = event.blacklisted;
    }

    public EventFull(Event event, Metadata metadata) {
        this(event);

        this.seen = metadata.seen;
        this.notified = metadata.notified;
        this.addedDate = metadata.addedDate;
    }

    public int getColor() {
        return color == -1 ? typeColor : color;
    }

    @Override
    public String toString() {
        return "EventFull{" +
                "profileId=" + profileId +
                ", id=" + id +
                ", eventDate=" + eventDate +
                ", startTime=" + startTime +
                ", topic='" + topic + '\'' +
                ", color=" + color +
                ", type=" + type +
                ", addedManually=" + addedManually +
                ", sharedBy='" + sharedBy + '\'' +
                ", sharedByName='" + sharedByName + '\'' +
                ", blacklisted=" + blacklisted +
                ", teacherId=" + teacherId +
                ", subjectId=" + subjectId +
                ", teamId=" + teamId +
                ", typeName='" + typeName + '\'' +
                ", teacherFullName='" + teacherFullName + '\'' +
                ", subjectLongName='" + subjectLongName + '\'' +
                ", subjectShortName='" + subjectShortName + '\'' +
                ", teamName='" + teamName + '\'' +
                ", seen=" + seen +
                ", notified=" + notified +
                ", addedDate=" + addedDate +
                '}';
    }
}
