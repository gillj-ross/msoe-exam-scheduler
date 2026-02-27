import React from 'react';

/**
 * Displays a single exam row with a checkbox for selection.
 * Props:
 *   exam     - the exam object
 *   selected - boolean, whether this exam is selected
 *   onToggle - callback(id) when checkbox changes
 */
function ExamCard({ exam, selected, onToggle }) {
  const formatTime = (time) => {
    if (!time) return '—';
    const [h, m] = time.split(':');
    const hour = parseInt(h, 10);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${m} ${ampm}`;
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  return (
    <tr className={`exam-card ${selected ? 'selected' : ''}`}>
      <td>
        <input
          type="checkbox"
          checked={selected}
          onChange={() => onToggle(exam.id)}
          aria-label={`Select ${exam.courseCode}`}
        />
      </td>
      <td className="course-code">{exam.courseCode}</td>
      <td>{exam.courseSection || '—'}</td>
      <td>{exam.courseName || '—'}</td>
      <td>{exam.instructor || '—'}</td>
      <td>{formatDate(exam.examDate)}</td>
      <td>
        {formatTime(exam.startTime)} – {formatTime(exam.endTime)}
      </td>
      <td>{exam.location || '—'}</td>
      <td>{exam.semester || '—'}</td>
    </tr>
  );
}

export default ExamCard;
