import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
});

export const getExams = (search, semester) =>
  api.get('/api/exams', { params: { search, semester } });

export const getExamById = (id) =>
  api.get(`/api/exams/${id}`);

export const createExam = (exam) =>
  api.post('/api/exams', exam);

export const updateExam = (id, exam) =>
  api.put(`/api/exams/${id}`, exam);

export const deleteExam = (id) =>
  api.delete(`/api/exams/${id}`);

export const getGoogleCalendarUrl = (id) =>
  api.get(`/api/exams/${id}/google-calendar-url`);

export const exportIcs = (ids) =>
  api.get('/api/exams/export/ics', {
    params: { ids: ids.join(',') },
    responseType: 'blob',
  });

export const getWebcalUrl = () =>
  api.get('/api/exams/webcal-url');
