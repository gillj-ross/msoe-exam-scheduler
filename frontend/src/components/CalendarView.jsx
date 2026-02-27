import React, { useState, useEffect, useCallback } from 'react';
import { Calendar, dateFnsLocalizer } from 'react-big-calendar';
import { format, parse, startOfWeek, getDay } from 'date-fns';
import { enUS } from 'date-fns/locale';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { getExams } from '../api/examApi';
import ExportButtons from './ExportButtons';

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek: () => startOfWeek(new Date(), { weekStartsOn: 0 }),
  getDay,
  locales: { 'en-US': enUS },
});

/**
 * Monthly calendar view showing exams as events.
 * Clicking an event selects/deselects it for export.
 */
function CalendarView() {
  const [exams, setExams] = useState([]);
  const [events, setEvents] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    getExams()
      .then((res) => {
        const data = res.data;
        setExams(data);
        setEvents(data.map(examToEvent));
      })
      .catch(() => setError('Failed to load exam calendar.'));
  }, []);

  const examToEvent = (exam) => {
    const startDate = exam.examDate ? new Date(exam.examDate + 'T00:00:00') : new Date();
    const [sh, sm] = exam.startTime ? exam.startTime.split(':') : [8, 0];
    const [eh, em] = exam.endTime ? exam.endTime.split(':') : [10, 0];

    const start = new Date(startDate);
    start.setHours(parseInt(sh, 10), parseInt(sm, 10), 0);

    const end = new Date(startDate);
    end.setHours(parseInt(eh, 10), parseInt(em, 10), 0);

    return {
      id: exam.id,
      title: `${exam.courseCode} Final${exam.location ? ` — ${exam.location}` : ''}`,
      start,
      end,
      resource: exam,
    };
  };

  const handleSelectEvent = useCallback((event) => {
    setSelectedIds((prev) =>
      prev.includes(event.id)
        ? prev.filter((x) => x !== event.id)
        : [...prev, event.id]
    );
  }, []);

  const eventStyleGetter = (event) => {
    const isSelected = selectedIds.includes(event.id);
    return {
      style: {
        backgroundColor: isSelected ? '#1a56db' : '#3b82f6',
        borderLeft: isSelected ? '4px solid #1e40af' : '4px solid #3b82f6',
        color: '#fff',
        borderRadius: '4px',
        opacity: isSelected ? 1 : 0.85,
      },
    };
  };

  return (
    <div className="calendar-view">
      {error && <p className="status-msg error">{error}</p>}

      <p className="calendar-hint">
        Click an exam to select it, then use the export buttons below.
      </p>

      <ExportButtons selectedIds={selectedIds} />

      <div className="calendar-container">
        <Calendar
          localizer={localizer}
          events={events}
          startAccessor="start"
          endAccessor="end"
          style={{ height: 600 }}
          onSelectEvent={handleSelectEvent}
          eventPropGetter={eventStyleGetter}
          views={['month', 'week', 'agenda']}
          defaultView="month"
          defaultDate={new Date(2026, 4, 1)}
          popup
          tooltipAccessor={(event) => {
            const e = event.resource;
            return `${e.courseCode} — ${e.courseName}\nInstructor: ${e.instructor}\nRoom: ${e.location}`;
          }}
        />
      </div>
    </div>
  );
}

export default CalendarView;
